package com.yeshen.appcenter.service;

import com.yeshen.appcenter.domain.document.HeaderLinkDocument;

import java.util.List;

/**
 * Date 2022/05/19  10:54
 * author  by HuBingKuan
 */
public interface HeaderLinkService {
    List<HeaderLinkDocument> findHeaderLinks(String region);
}