package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.domain.document.HeaderLinkDocument;
import com.yeshen.appcenter.service.HeaderLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Date 2022/05/19  11:16
 * author  by HuBingKuan
 */
@Validated
@RestController
@RequestMapping("/headerLink")
@RequiredArgsConstructor
@ConditionalOnExpression("'${spring.application.name}'.equals('WebAppcenterService')")
public class HeaderLinkController {
    private final HeaderLinkService headerLinkService;

    /**
     * 根据region获取头部链接信息
     */
    @GetMapping("/list")
    public List<HeaderLinkDocument> getHeaderLinks(@NotBlank @RequestHeader("locale")String locale) {
        return headerLinkService.findHeaderLinks(locale);
    }
}