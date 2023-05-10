package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.vo.response.BannerVO;
import com.yeshen.appcenter.domain.vo.response.GameDisplayRepVO;
import com.yeshen.appcenter.domain.vo.response.IndexGameDisplayListRepVO;
import com.yeshen.appcenter.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date 2022/1/25/0025
 * author by HuBingKuan
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/index")
@RequiredArgsConstructor
public class IndexController {
    private final GameService gameService;

    /**
     * 获取首页banner
     */
    @GetMapping(value = "/banner")
    public List<BannerVO> getBanner() {
        return gameService.getBannerDao(DataSourceContextHolder.getDatabaseHolder());
    }

    /**
     * 获取首页推荐游戏列表
     */
    @GetMapping("/game/recommend")
    public IndexGameDisplayListRepVO getRecommendGame() {
        return gameService.getRecommendGameDao(DataSourceContextHolder.getDatabaseHolder());
    }


    /**
     * 获取首页游戏下载top10
     */
    @GetMapping("/game/download/top10")
    public List<GameDisplayRepVO> getGameDownloadTopTen() {
        return gameService.getGameDownloadTopTenDao(DataSourceContextHolder.getDatabaseHolder());
    }
}