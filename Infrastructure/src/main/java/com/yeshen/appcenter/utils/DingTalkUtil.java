package com.yeshen.appcenter.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Date 2022/06/22  20:23
 * author  by HuBingKuan
 */
@Slf4j
public class DingTalkUtil {
    private static String webhookUrl = "https://oapi.dingtalk.com/robot/send?access_token=90ba55288a14990f756dbfe12cd01bb731223cbef345c206412ff8379edd46e4";

    /**
     * msg必须包含关键词
     *
     * @param msg
     */
    public static void sendMessage(String msg, boolean notice) {
        JSONObject bodys = new JSONObject();
        bodys.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", msg);
        bodys.put("text", text);
        JSONObject at = new JSONObject();
        // 是否at所有人
        at.put("isAtAll", false);
        if (notice) {
            at.put("atMobiles", Arrays.asList("19986258610"));
        }
        bodys.put("at", at);
        String requestBody = JSON.toJSONString(bodys);
        String response = HttpClientUtil.postRequest(webhookUrl, requestBody, String.class);
        log.info("钉钉报警返回消息:{}", response);
    }
}