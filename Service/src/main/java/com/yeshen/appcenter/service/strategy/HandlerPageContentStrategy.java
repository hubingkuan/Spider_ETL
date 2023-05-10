package com.yeshen.appcenter.service.strategy;

import com.yeshen.appcenter.domain.vo.response.GameBaseArticle;

/**
 * Date 2023/01/11  17:34
 * author  by HuBingKuan
 * 处理文章内容策略类
 */
public interface HandlerPageContentStrategy {
    String handlePageContent(String content);

    void insertPageContent(GameBaseArticle article);
}