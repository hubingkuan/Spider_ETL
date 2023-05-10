package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.entity.Label;
import com.yeshen.appcenter.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date 2022/1/24/0024
 * author by HuBingKuan
 */
@Validated
@RestController
@RequestMapping("/label")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    /**
     * 获取所有不重复的标签
     */
    @GetMapping()
    public List<Label> getAllLabel() {
        return labelService.getDistinctLabelsByParentIdDao(DataSourceContextHolder.getDatabaseHolder());
    }
}