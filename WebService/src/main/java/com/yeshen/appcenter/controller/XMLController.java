package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.annotation.NotControllerResponseAdvice;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.vo.response.XMLVO;
import com.yeshen.appcenter.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date 2022/06/07  19:24
 * author  by HuBingKuan
 */
@RequestMapping("/xml")
@RestController
@RequiredArgsConstructor
public class XMLController {
    private final SpringTemplateEngine springTemplateEngine;
    private final GameService gameService;

    @GetMapping(value = "/getSiteMap",produces = MediaType.APPLICATION_XML_VALUE)
    @NotControllerResponseAdvice
    public String getSiteMapXml(){
        String dataSourceName = DataSourceContextHolder.getDatabaseHolder();
        String region = dataSourceName.split("_")[1].toLowerCase();
        String homeLocation = SystemConstant.SITEMAP_REGIONS.get(region);
        Context context = new Context();
        List<XMLVO> result = gameService.getSiteMapInfo(homeLocation);
        Map<String, Object> map = new HashMap<>(1);
        map.put("XMLVOList",result);
        context.setVariables(map);
        return springTemplateEngine.process("SiteMap",context);
    }
}