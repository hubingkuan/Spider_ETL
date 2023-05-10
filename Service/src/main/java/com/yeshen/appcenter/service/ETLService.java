package com.yeshen.appcenter.service;

import com.yeshen.appcenter.domain.vo.response.GameBaseArticle;

import java.util.List;

/**
 * Date 2022/07/07  19:28
 * author  by HuBingKuan
 */
public interface ETLService {
    Integer fillingWordPressApp(String isoCode,String day);

    String updateIcon(String isoCode,String packageName);

    List<String> fillingGameBase(List<GameBaseArticle> articles);

    void fillingAppByPackageAndRegion(String isoCode, String packageName);

    void CrawlingTaptapApp() throws InterruptedException;

}