package com.yeshen.appcenter.config.web.listener;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.utils.SendMailUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Date 2022/04/02  13:52
 * author by HuBingKuan
 */
@Component
public class InterfaceResultErrorListener implements ApplicationListener<InterfaceResultErrorEvent> {
    @Override
    public void onApplicationEvent(InterfaceResultErrorEvent event) {
        SendMailUtil.sendSimpleMail("maguahu2000@163.com",SystemConstant.MAIL_SUBJECT_RESULT_ERROR, event.getRequestPath() + "\n" + event.getRequestBody());
    }
}