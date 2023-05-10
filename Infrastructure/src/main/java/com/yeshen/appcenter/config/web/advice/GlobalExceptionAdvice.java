package com.yeshen.appcenter.config.web.advice;

import com.alibaba.fastjson.JSON;
import com.yeshen.appcenter.config.executor.ThreadPoolTaskFuture;
import com.yeshen.appcenter.config.web.listener.InterfaceResultErrorEvent;
import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.common.ResultVO;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.enums.ResultCode;
import com.yeshen.appcenter.utils.HttpContextUtil;
import com.yeshen.appcenter.utils.SpringContextUtil;
import com.yeshen.appcenter.utils.ThrowableUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class GlobalExceptionAdvice {
    private final static String PREFIX = "com";

    private final static String ENV_PROD = "prd";

    @Value("${spring.profiles.active}")
    private String profile;

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResultVO exceptionHandler(HttpServletRequest req, Object e) {
        List<ValidatedFieldMsg> errorMessage = new ArrayList<>();
        if (e instanceof MethodArgumentNotValidException) {
            // 对于RequestBody进行参数校验 使用@Valid和@Validated都行(推荐@Validated,因为有分组校验的功能)
            MethodArgumentNotValidException methodException = (MethodArgumentNotValidException) e;
            errorMessage.addAll(methodException.getBindingResult().getFieldErrors().stream()
                    .map(error -> new ValidatedFieldMsg().setField(error.getField())
                            .setValue(error.getRejectedValue())
                            .setMessage(error.getDefaultMessage()))
                    .collect(Collectors.toList()));
            log.warn("请求体校验异常，请求地址:{},请求主机:{},请求参数:{}", req.getRequestURI(), req.getRemoteAddr(), JSON.toJSON(HttpContextUtil.getBodyString(req)));
        } else if (e instanceof ConstraintViolationException) {
            // 对RequestParam进行参数校验 在整个类上使用@Validated 在方法和参数上使用无效
            ConstraintViolationException constraintException = (ConstraintViolationException) e;
            errorMessage.addAll(constraintException.getConstraintViolations().stream()
                    .map(error -> new ValidatedFieldMsg().setField(StringUtils.substringAfter(error.getPropertyPath().toString(), "."))
                            .setValue(error.getInvalidValue())
                            .setMessage(error.getMessage()))
                    .collect(Collectors.toList()));
            log.warn("请求参数校验异常，请求地址:{},请求主机:{},请求参数:{}", req.getRequestURI(), req.getRemoteAddr(), JSON.toJSON(HttpContextUtil.getParameterMap(req)));
        } else if (e instanceof IllegalArgumentException) {
            // 对Assert进行业务结果校验 注意使用的是spring的Assert
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) e;
            log.warn("Assert校验异常,请求地址:{},请求主机:{}", req.getRequestURI(), req.getRemoteAddr());
            return ResultVO.createError(ResultCode.ASSERT_ERROR, illegalArgumentException.getMessage());
        }
        return ResultVO.createError(ResultCode.MethodArgumentNotValid, JSON.toJSONString(errorMessage));
    }


    /**
     * 处理数据库查询语句错误
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {BadSqlGrammarException.class})
    public ResultVO exceptionHandler(HttpServletRequest req, BadSqlGrammarException e) {
        log.error("URL:{},locale:{},发生数据库查询错误!查询语句:{}", req.getRequestURI(), req.getHeader(SystemConstant.REQUEST_HEADER_REGION), e.getSql(), e);
        ThreadPoolTaskFuture.runAsync(() -> publishErrorEvent(req));
        if (ENV_PROD.equals(profile)) {
            return ResultVO.createError(ResultCode.SERVER_ERROR);
        }
        return new ResultVO(ResultCode.SERVER_ERROR.getCode(), e.getMessage());
    }


    //处理Controller上一层的异常
    @ExceptionHandler({
            // 404异常
            NoHandlerFoundException.class,
            // 请求方法错误异常
            HttpRequestMethodNotSupportedException.class,
            // 请求头content-type不支持返回的响应格式(比如application/json)
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            // 未检测到路径参数,比如@PathVariable中必要的参数
            MissingPathVariableException.class,
            // 缺少请求参数 比如@RequestParam
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            // 参数类型匹配失败，比如接收的是Long类型，但传入的是String
            TypeMismatchException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            // 异步请求超时
            AsyncRequestTimeoutException.class,
            BindException.class
    })
    public ResultVO handlerServletException(HttpServletRequest req, Exception e) {
        log.warn("URL:{},locale:{},Controller上层异常:{},原因是:{}", req.getRequestURI(), req.getHeader(SystemConstant.REQUEST_HEADER_REGION), e.getClass().getSimpleName(), e.getMessage(), e);
        return new ResultVO(ResultCode.REQUEST_ERROR.getCode(), e.getMessage());
    }


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResultVO exceptionHandler(HttpServletRequest req, BusinessException e) {
        log.warn("URL:{},locale:{},发生业务异常！错误提示消息:{}", req.getRequestURI(), req.getHeader(SystemConstant.REQUEST_HEADER_REGION), e.getErrorMsg(), e);
        return ResultVO.createError(e);
    }


    /**
     * 通用异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResultVO exceptionHandler(HttpServletRequest req, Exception e) {
        log.warn("URL:{},locale:{},通用异常!原因是:{}", req.getRequestURI(), req.getHeader(SystemConstant.REQUEST_HEADER_REGION), ThrowableUtil.getStackTraceByPackage(e, PREFIX));
        ThreadPoolTaskFuture.runAsync(() -> publishErrorEvent(req));
        if (ENV_PROD.equals(profile)) {
            return ResultVO.createError(ResultCode.SERVER_ERROR);
        }
        return new ResultVO(ResultCode.SERVER_ERROR.getCode(), e.getMessage());
    }

    @Data
    @Accessors(chain = true)
    public class ValidatedFieldMsg {
        private String field;
        private Object value;
        private String message;
    }

    public void publishErrorEvent(HttpServletRequest request) {
        /*ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);*/
        InterfaceResultErrorEvent event = new InterfaceResultErrorEvent(new Object());
        event.setRequestPath(request.getServletPath());
        if (request.getMethod().equals(HttpMethod.GET.name())) {
            event.setRequestBody(JSON.toJSONString(HttpContextUtil.getParameterMap(request)));
        } else if (request.getMethod().equals(HttpMethod.POST.name())) {
            event.setRequestBody(JSON.toJSONString(HttpContextUtil.getBodyString(request)));
        }
        SpringContextUtil.publishEvent(event);
    }
}