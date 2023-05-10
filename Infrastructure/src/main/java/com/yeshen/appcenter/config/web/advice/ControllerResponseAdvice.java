package com.yeshen.appcenter.config.web.advice;

import com.alibaba.fastjson.JSON;
import com.yeshen.appcenter.domain.annotation.NotControllerResponseAdvice;
import com.yeshen.appcenter.domain.common.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 * 全局统一返回值处理 这里只处理Controller层面的返回 不处理全局异常的返回(basePackages定义)
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.yeshen.appcenter.controller"})
public class ControllerResponseAdvice implements ResponseBodyAdvice {
    /**
     * 判断是否要执行 beforeBodyWrite 方法，true为执行，false不执行
     * 如果是重定向和转发就不需要统一封装了
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // response是ResultVo类型，或者注释了NotControllerResponseAdvice都不进行包装
        return !(methodParameter.getParameterType().isAssignableFrom(ResultVO.class)
                || methodParameter.hasMethodAnnotation(NotControllerResponseAdvice.class));
    }

    /**
     * 处理response的具体业务方法
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest request, ServerHttpResponse response) {
        // 返回为String类型需要特殊处理 不然报错java.lang.ClassCastException:ResultVO cannot be cast to String
        if(methodParameter.getGenericParameterType().equals(String.class)){
            return JSON.toJSONString(ResultVO.createSuccess(o));
        }
        return ResultVO.createSuccess(o);
    }
}