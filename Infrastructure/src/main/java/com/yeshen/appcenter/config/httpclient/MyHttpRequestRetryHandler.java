package com.yeshen.appcenter.config.httpclient;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Date 2022/06/29  10:23
 * author  by HuBingKuan
 */
@Slf4j
public class MyHttpRequestRetryHandler extends StandardHttpRequestRetryHandler {
    public MyHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
        super(retryCount, requestSentRetryEnabled);
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        String uri = request.getRequestLine().getUri();
        //如果已经执行的次数大于设置的次数，则不继续重试
        if (executionCount > getRetryCount()) {
            log.warn("尝试重试第{}次,但超出重试次数,调用url:{},异常信息:{}", executionCount, uri, exception.getMessage());
            return false;
        }
        // 超时请求、找不到对应的host、找到了host但建立连接失败、https认证异常、服务端断开连接并无通知客户端
        if (exception instanceof SocketTimeoutException || exception instanceof UnknownHostException
                || exception instanceof SSLException || exception instanceof NoHttpResponseException) {
            log.warn("可接受的异常,进行了重试,重试次数:{},调用url:{},异常信息:{}", executionCount, uri, exception.getMessage());
            return true;
        }
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        if (idempotent) {
            // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
            log.warn("方法幂等,进行了重试,重试次数:{},调用url:{},异常信息:{}", executionCount, uri, exception.getMessage());
            return true;
        }
        return false;
    }
}