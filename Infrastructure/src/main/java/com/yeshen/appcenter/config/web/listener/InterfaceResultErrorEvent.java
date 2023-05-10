package com.yeshen.appcenter.config.web.listener;

import org.springframework.context.ApplicationEvent;

/**
 * Date 2022/04/02  13:44
 * author by HuBingKuan
 * 接口返回报错事件
 */
public class InterfaceResultErrorEvent extends ApplicationEvent {
    private String requestPath;

    private String requestBody;

    public InterfaceResultErrorEvent(Object source) {
        super(source);
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}