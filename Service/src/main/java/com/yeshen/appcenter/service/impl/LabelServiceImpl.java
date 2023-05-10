package com.yeshen.appcenter.service.impl;

import com.yeshen.appcenter.config.executor.ThreadPoolTaskFuture;
import com.yeshen.appcenter.domain.entity.Label;
import com.yeshen.appcenter.repository.mysql.LabelDAO;
import com.yeshen.appcenter.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelDAO labelDAO;

    @Override
    @Cacheable(key="#prefix",value = "all_label")
    public List<Label> getDistinctLabelsByParentIdDao(String prefix) {
        CompletableFuture<List<Label>> future = ThreadPoolTaskFuture.supplyAsync(labelDAO::getRootLabelId)
                .thenApply(labelDAO::getDistinctLabelsByParentId);
        return future.join();
    }
}