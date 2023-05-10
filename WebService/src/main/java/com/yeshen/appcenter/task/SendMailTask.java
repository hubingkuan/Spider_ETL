// package com.yeshen.appcenter.task;
//
// import com.alibaba.fastjson.JSONObject;
// import com.yeshen.appcenter.utils.MyHttpClientUtil;
// import com.yeshen.appcenter.utils.SendMailUtil;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// /**
//  * Date 2022/04/02  11:46
//  * author by HuBingKuan
//  */
// @Slf4j
// @Component
// @ConditionalOnProperty(prefix = "spring.mail", name = "enable", havingValue = "true")
// public class SendMailTask {
//     @Autowired
//     private MyHttpClientUtil httpClientUtil;
//
//
//     @Async("asyncTaskExecutor")
//     @Scheduled(cron = "0 */20 * * * ?")
//     public void checkHealth() {
//         log.info("=========开始健康检查=========");
//         JSONObject result = httpClientUtil.getRequest("http://localhost:7777/appcenter/manage/health", JSONObject.class);
//         if (result == null) {
//             SendMailUtil.sendSimpleMail("WebAppcenter健康检查", "健康检查出错");
//             return;
//         }
//         String status = result.getString("status");
//         log.info("健康检查状态:{}", status);
//         if ("DOWN".equals(status)) {
//             SendMailUtil.sendSimpleMail("WebAppcenter健康检查", "健康检查出错");
//         }
//     }
// }