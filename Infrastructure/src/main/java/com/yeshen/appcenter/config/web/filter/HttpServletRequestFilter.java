package com.yeshen.appcenter.config.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Date 2022/1/24/0024
 * author by HuBingKuan
 * request.getInputStream()只能读取一次，所以当使用全局异常处理器时，
 * 已经不能获取body中的参数。所以需要增加一个过滤器来解决
 */
@Slf4j
public class HttpServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // 将请求流转换为可多次读取的请求流
        ServletRequest repeatReadRequest = new RepeatReadRequest(httpServletRequest);
        filterChain.doFilter(repeatReadRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}