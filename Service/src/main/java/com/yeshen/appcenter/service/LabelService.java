package com.yeshen.appcenter.service;

import com.yeshen.appcenter.domain.entity.Label;

import java.util.List;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public interface LabelService {
    /**
     * 按父 ID 获取不同的标签
     *
     * @return
     */
    List<Label> getDistinctLabelsByParentIdDao(String prefix);
}