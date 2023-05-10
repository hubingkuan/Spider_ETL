package com.yeshen.appcenter.task;

import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import com.yeshen.appcenter.service.GameService;
import com.yeshen.appcenter.service.LabelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 */

@Slf4j
@Component
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@RequiredArgsConstructor
public class ApplicationStartTask implements ApplicationRunner {
    private final GameService gameService;

    private final LabelService labelService;

    private static Set<SupportDatasourceEnum> supportDataSources;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @PostConstruct
    public void init() {
        if (SystemConstant.ENV_DEV.equals(activeProfile)) {
            supportDataSources = SupportDatasourceEnum.getServiceDevSupportDatasource();
        } else {
            supportDataSources = SupportDatasourceEnum.getServicePrdSupportDatasource();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("项目启动开始加载热数据");
        loadHotData();
        log.info("加载热数据结束");
    }


    private void loadHotData() {
        for (final SupportDatasourceEnum supportDatasource : supportDataSources) {
            if (supportDatasource.equals(SupportDatasourceEnum.PRD_REPTILE)) {
                continue;
            }
            DataSourceContextHolder.setDatabaseHolder(supportDatasource);
            log.info("查询{}首页推荐游戏", supportDatasource.name());
            gameService.getRecommendGameDao(supportDatasource.name());
            log.info("查询{}首页banner", supportDatasource.name());
            gameService.getBannerDao(supportDatasource.name());
            log.info("查询{}下载排行榜Top10", supportDatasource.name());
            gameService.getGameDownloadTopTenDao(supportDatasource.name());
            log.info("查询{}标签", supportDatasource.name());
            labelService.getDistinctLabelsByParentIdDao(supportDatasource.name());
        }
    }
}



/*    @Async("asyncTaskExecutor")
    public void refreshCacheData() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(e -> {
            Optional<Cache> optional = Optional.ofNullable(cacheManager.getCache(e));
            optional.ifPresent(Cache::invalidate);
        });
        log.info("开始更新热数据");
        loadHotData();
        log.info("更新热数据结束");
    }*/

/*
    private List<String> buildLuaKey(String region){
        List<String> keyList = new ArrayList();
        keyList.add(region+ RedisKeyConstant.RECOMMEND_GAME);
        keyList.add(region + RedisKeyConstant.ALL_BANNER);
        keyList.add(region + RedisKeyConstant.ALL_GAME_DOWNLOAD_TOP_TEN);
        keyList.add(region + RedisKeyConstant.ALL_LABEL);
        return keyList;
    }

    private Map<String,Object> buildLuaArgs(IndexGameDisplayListRepVO recommendGame, List<BannerVO> banner, List<GameDisplayRepVO> gameDownloadTop, List<Label> labels){
        Map<String,Object> argvMap = new HashMap();
        argvMap.put("indexGames", JSON.toJSONString(recommendGame, SerializerFeature.WriteClassName));
        argvMap.put("banner",JSON.toJSONString(banner, SerializerFeature.WriteClassName));
        argvMap.put("downloadGames",JSON.toJSONString(gameDownloadTop, SerializerFeature.WriteClassName));
        argvMap.put("labels",JSON.toJSONString(labels, SerializerFeature.WriteClassName));
        return argvMap;
    }
    redisTemplate.execute(luaScript,valueSerializer,resultSerializer,buildLuaKey(region), buildLuaArgs(recommendGame,banner,gameDownloadTop,labels));
*/