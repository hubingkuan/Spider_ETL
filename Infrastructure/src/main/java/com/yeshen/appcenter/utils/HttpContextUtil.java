package com.yeshen.appcenter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Date 2022/1/24/0024
 * author by HuBingKuan
 */
@Slf4j
public class HttpContextUtil {
    /**
     * 获取request请求对象
     * @return
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    /**
     * 获取query参数
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Enumeration<String> parameters = request.getParameterNames();
        Map<String, String> params = new HashMap<>(8);
        while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
            String value = request.getParameter(parameter);
            params.put(parameter, value);
        }
        return params;
    }

    /**
     * 获取body参数
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader=new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.warn("获取请求体数据错误:{}",e.getMessage(),e);
        }
        return sb.toString();
    }
}