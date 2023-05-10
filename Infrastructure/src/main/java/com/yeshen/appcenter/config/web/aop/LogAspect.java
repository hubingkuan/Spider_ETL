package com.yeshen.appcenter.config.web.aop;

import com.alibaba.fastjson.JSON;
import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.utils.HttpContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;

/**
 * Date 2022/2/23/0023
 * author by HuBingKuan
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    /**
     * AOP注解执行顺序
     * 正常情况：@Before=====目标方法=====@After=====@AfterReturning
     * 异常情况：@Before=====目标方法=====@After=====@AfterThrowing
     * <p>
     * 5大注解执行顺序
     * 正常情况: 环绕前置===@Before====目标方法===环绕返回====环绕最终====@After====@AfterReturning
     * 异常情况: 环绕前置===@Before====目标方法===环绕返回====环绕最终====@After====@AfterThrowing
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@within(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointCut() {
    }


    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTimeMillis = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object result = joinPoint.proceed();
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setLocale(request.getHeader(SystemConstant.REQUEST_HEADER_REGION));
        requestInfo.setIp(request.getRemoteAddr());
        requestInfo.setUrl(request.getRequestURL().toString());
        requestInfo.setHttpMethod(request.getMethod());
        requestInfo.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()));
        if (HttpMethod.POST.name().equals(request.getMethod())) {
            requestInfo.setRequestParams(getNameAndValue(joinPoint));
        } else if (HttpMethod.GET.name().equals(request.getMethod())) {
            requestInfo.setRequestParams(HttpContextUtil.getParameterMap(request));
        }
        requestInfo.setTimeCost(System.currentTimeMillis() - startTimeMillis);
        log.info("Request Info: {}", JSON.toJSONString(requestInfo));
        return result;
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        RequestErrorInfo requestErrorInfo = new RequestErrorInfo();
        requestErrorInfo.setLocale(request.getHeader(SystemConstant.REQUEST_HEADER_REGION));
        requestErrorInfo.setIp(getIp(request));
        requestErrorInfo.setUrl(request.getRequestURL().toString());
        requestErrorInfo.setHttpMethod(request.getMethod());
        requestErrorInfo.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()));
        if (HttpMethod.POST.name().equals(request.getMethod())) {
            requestErrorInfo.setRequestParams(getNameAndValue(joinPoint));
        } else if (HttpMethod.GET.name().equals(request.getMethod())) {
            requestErrorInfo.setRequestParams(HttpContextUtil.getParameterMap(request));
        }
        // completableFuture包装的异常特殊处理
        if (e instanceof CompletionException && e.getCause() != null) {
            requestErrorInfo.setErrorMessage(e.getCause().getMessage());
            log.warn("Error Request Info: {}", JSON.toJSONString(requestErrorInfo));
            throw (BusinessException) e.getCause();
        } else if (e instanceof BusinessException) {
            requestErrorInfo.setErrorMessage(((BusinessException) e).getErrorMsg());
        } else {
            requestErrorInfo.setErrorMessage(e.getMessage());
        }
        log.warn("Error Request Info: {}", JSON.toJSONString(requestErrorInfo));
    }

    private static final String UNKNOWN = "unknown";

    /**
     * 获取ip地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String comma = ",";
        String localhost = "127.0.0.1";
        if (ip.contains(comma)) {
            ip = ip.split(",")[0];
        }
        if (localhost.equals(ip)) {
            // 获取本机真正的ip地址
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.error("获取ip地址失败,错误信息:{}", e.getMessage(), e);
            }
        }
        return ip;
    }

    /**
     * 获取方法参数名和参数值
     *
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getNameAndValue(JoinPoint  joinPoint) {
        final Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        final String[] names = methodSignature.getParameterNames();
        final Object[] args = joinPoint.getArgs();

        if (names == null || args == null || names.length == 0 || args.length == 0) {
            return Collections.emptyMap();
        }
        if (names.length != args.length) {
            log.warn("{}方法参数名和参数值数量不一致", methodSignature.getName());
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], args[i]);
        }
        return map;
    }

    @Data
    public class RequestInfo {
        private String ip;
        private String url;
        private String httpMethod;
        private String classMethod;
        private Object requestParams;
        private Object result;
        private Long timeCost;
        private String locale;
    }

    @Data
    public class RequestErrorInfo {
        private String ip;
        private String url;
        private String httpMethod;
        private String classMethod;
        private Object requestParams;
        private String errorMessage;
        private String locale;
    }
}