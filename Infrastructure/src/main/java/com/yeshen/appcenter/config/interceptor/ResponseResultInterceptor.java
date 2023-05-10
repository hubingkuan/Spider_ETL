package com.yeshen.appcenter.config.interceptor;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 * 请求接口拦截
 */
public class ResponseResultInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> clazz = handlerMethod.getBeanType();
            // 判断是否在该类上面加特定的注解
            if(clazz.isAnnotationPresent(RestController.class)||clazz.isAnnotationPresent(ResponseBody.class)){
                request.setAttribute(SystemConstant.NEED_RESPONSE_RESULT,true);
            }
        }
        return true;
    }
}