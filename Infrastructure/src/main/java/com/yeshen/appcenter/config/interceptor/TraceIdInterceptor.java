package com.yeshen.appcenter.config.interceptor;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.utils.NanoIdUtil;
import org.slf4j.MDC;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date 2022/04/13  12:59
 * author by HuBingKuan
 * 日志拦截添加traceId
 */
public class TraceIdInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //如果有上层调用就用上层的ID
        String traceId = request.getHeader(SystemConstant.TRACE_ID);
        if (traceId == null) {
            traceId = NanoIdUtil.randomNanoId();
        }
        MDC.put(SystemConstant.TRACE_ID, traceId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        MDC.remove(SystemConstant.TRACE_ID);
    }
}