package com.yeshen.appcenter.config.httpclient;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Date 2022/10/25  14:15
 * author  by HuBingKuan
 */
@Slf4j
public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        //当前线程调用中有traceId，则将该traceId进行透传
        String traceId = MDC.get(SystemConstant.TRACE_ID);
        if (traceId != null) {
            //请求自身的话添加请求头traceId
            headers.add(SystemConstant.TRACE_ID, traceId);
        }
        if(!MediaType.APPLICATION_JSON_VALUE.equals(headers.getContentType())){
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        long start = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        log.info("Method:{},URL:{},Cost Time:{}ms",request.getMethod(),request.getURI(),System.currentTimeMillis() - start);
        if (log.isDebugEnabled()){
            log.debug("请求头:{}",headers);
        }
        return response;
    }
}