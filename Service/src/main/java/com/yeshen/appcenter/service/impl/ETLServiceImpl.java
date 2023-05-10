package com.yeshen.appcenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.Labels;
import com.yeshen.appcenter.alioss.OssModule;
import com.yeshen.appcenter.config.executor.ThreadPoolTaskFuture;
import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.entity.WPPost;
import com.yeshen.appcenter.domain.enums.ResultCode;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import com.yeshen.appcenter.domain.enums.Translate;
import com.yeshen.appcenter.domain.vo.response.GameBaseArticle;
import com.yeshen.appcenter.domain.vo.response.GameDisplayRepVO;
import com.yeshen.appcenter.domain.vo.response.ImageInfo;
import com.yeshen.appcenter.domain.vo.response.ReptileApp;
import com.yeshen.appcenter.repository.mysql.GameDAO;
import com.yeshen.appcenter.repository.mysql.PostMetaDAO;
import com.yeshen.appcenter.repository.mysql.ReptileDAO;
import com.yeshen.appcenter.service.ETLService;
import com.yeshen.appcenter.service.strategy.HandlerContentStrategyContext;
import com.yeshen.appcenter.service.strategy.HandlerPageContentStrategy;
import com.yeshen.appcenter.utils.ChineseCharacterHelper;
import com.yeshen.appcenter.utils.DingTalkUtil;
import com.yeshen.appcenter.utils.ThrowableUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Date 2022/07/07  19:29
 * author  by HuBingKuan
 */
@Slf4j
@Service
@ConditionalOnExpression("'${spring.application.name}'.equals('WebAppcenterETL')")
@RequiredArgsConstructor
public class ETLServiceImpl implements ETLService {
    private final ReptileDAO reptileDAO;

    private final PostMetaDAO postMetaDAO;

    private final OssModule ossModule;

    private final GameDAO gameDAO;


    @Qualifier("mysqlTransactionTemplate")
    private final TransactionTemplate transactionTemplate;

    @Autowired(required = false)
    private ChromeOptions chromeOptions;

    @Override
    public List<String> fillingGameBase(List<GameBaseArticle> articles) {
        List<String> result = new ArrayList<>();
        // 切换数据源
        SupportDatasourceEnum datasourceEnum = Optional.ofNullable(articles)
                .map(e -> e.get(0))
                .map(GameBaseArticle::getLanguage)
                .map(SystemConstant.GAME_BASE_DATABASE_MAPPING::get)
                .orElseThrow(() -> new BusinessException(ResultCode.REQUEST_ERROR));
        DataSourceContextHolder.setDatabaseHolder(datasourceEnum);
        HandlerPageContentStrategy strategy = HandlerContentStrategyContext.getStrategy(datasourceEnum);
        // 过滤空标题并且url去重
        articles = articles.stream()
                .filter(Objects::nonNull)
                .filter(e -> !StringUtils.isEmpty(e.getTitle()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(GameBaseArticle::getUrl))), ArrayList::new));
        for (final GameBaseArticle article : articles) {
            strategy.insertPageContent(article);
            result.add(article.getTitle());
        }
        log.info("一共{}篇文章,爬取完成{}篇", articles.size(), result.size());
        return result;
    }


    @Override
    public Integer fillingWordPressApp(String isoCode, String day) {
        long startTime = System.currentTimeMillis();
        // 统计每个地区插入app的数量
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Set<String> isoCodes = SystemConstant.CRAWLER_FETCH_REGIONS.keySet();
        if (!isoCodes.contains(isoCode)) {
            throw new BusinessException(ResultCode.REQUEST_ERROR, "isoCode错误");
        }
        SupportDatasourceEnum datasourceEnum = SystemConstant.CRAWLER_FETCH_REGIONS.get(isoCode);
        // 查询爬虫库的app 并且去重(equal不比较游戏更新时间以及id)
        List<ReptileApp> apps = getAppsFromReptileDB(isoCode, day);
        apps = apps.stream().distinct().collect(Collectors.toList());
        log.info("开始填充{}地区,最近{}天的需要更新的app数:{}", isoCode, day, apps.size());
        for (final ReptileApp app : apps) {
            String region = StringUtils.substringAfter(datasourceEnum.toString(), "_");
            // 异步上传图片
            CompletableFuture<String> future1 = ThreadPoolTaskFuture.supplyAsync(() -> ossModule.uploadPhotoByUrl(region, app.getLogoUrl()).getOssUrl());
            CompletableFuture<List<String>> future2 = ThreadPoolTaskFuture.supplyAsync(() -> getOssGamePhotosByOutImageList(region, app.getImageUrls()));
            fillWordpressDbByApp(app, datasourceEnum, atomicInteger, future1, future2);
        }
        int result = atomicInteger.get();
        long minutes = TimeUnit.MINUTES.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
        log.info("填充{}地区完成,更新成功的app数:{},耗时:{}分钟", isoCode, result, minutes);
        return result;
    }

    @Override
    public void fillingAppByPackageAndRegion(String isoCode, String packageName) {
        DataSourceContextHolder.setDatabaseHolder(SupportDatasourceEnum.PRD_REPTILE);
        SupportDatasourceEnum datasourceEnum = SystemConstant.CRAWLER_FETCH_REGIONS.get(isoCode);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ReptileApp app = reptileDAO.getReptileAppByPackageAndRegion(packageName, isoCode);
        String region = StringUtils.substringAfter(datasourceEnum.toString(), "_");
        CompletableFuture<String> future1 = ThreadPoolTaskFuture.supplyAsync(() -> ossModule.uploadPhotoByUrl(region, app.getLogoUrl()).getOssUrl());
        CompletableFuture<List<String>> future2 = ThreadPoolTaskFuture.supplyAsync(() -> getOssGamePhotosByOutImageList(region, app.getImageUrls()));
        fillWordpressDbByApp(app, datasourceEnum, atomicInteger, future1, future2);
    }

    /**
     * 查询指定天数更新的app数据
     *
     * @param isoCode
     */
    private List<ReptileApp> getAppsFromReptileDB(String isoCode, String day) {
        DataSourceContextHolder.setDatabaseHolder(SupportDatasourceEnum.PRD_REPTILE);
        List<ReptileApp> results = reptileDAO.getReptileAppListByRegion(isoCode, Integer.valueOf(day));
        // EN地区的app入库使用US以及PH的app集合
        if ("US".equals(isoCode)) {
            results.addAll(reptileDAO.getReptileAppListByRegion("PH", Integer.valueOf(day)));
        }
        return results;
    }

    @Override
    public String updateIcon(String isoCode, String packageName) {
        log.info("更新{}地区{}游戏的icon", isoCode, packageName);
        Set<String> isoCodes = SystemConstant.CRAWLER_FETCH_REGIONS.keySet();
        if (!isoCodes.contains(isoCode)) {
            throw new BusinessException(ResultCode.REQUEST_ERROR, "isoCode错误");
        }
        DataSourceContextHolder.setDatabaseHolder(SupportDatasourceEnum.PRD_REPTILE);
        String logoUrl = reptileDAO.getLogoUrlByPackageAndRegion(packageName, isoCode);
        if (logoUrl == null) {
            throw new BusinessException(ResultCode.IMAGE_NOT_EXIST);
        }
        SupportDatasourceEnum datasourceEnum = SystemConstant.CRAWLER_FETCH_REGIONS.get(isoCode);
        String region = StringUtils.substringAfter(datasourceEnum.toString(), "_");
        String ossUrl = ossModule.uploadPhotoByUrl(region, logoUrl).getOssUrl();
        if (ossUrl == null) {
            throw new BusinessException(ResultCode.GAME_NOT_EXIST, "爬虫库不存在该游戏");
        }
        DataSourceContextHolder.setDatabaseHolder(datasourceEnum);
        Long postId = postMetaDAO.getPostIdByMetaKeyAndMetaValue("apkPackage", packageName);
        if (postId == null) {
            throw new BusinessException(ResultCode.GAME_NOT_EXIST, "wp库不存在该游戏");
        }
        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "gameIcon", ossUrl);
        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "gameLogo", ossUrl);
        return "更新完成";
    }


    /*
     * 导致事务回滚的方式：
     * 1、函数出现异常
     * 2、transactionStatus.setRollbackOnly();
     * */
    private void fillWordpressDbByApp(ReptileApp app, SupportDatasourceEnum datasource, AtomicInteger atomicInteger, CompletableFuture<String> future1, CompletableFuture<List<String>> future2) {
        String region = StringUtils.substringAfter(datasource.toString(), "_");
        // 开启事务之前先切换数据源
        DataSourceContextHolder.setDatabaseHolder(datasource);
        LocalDateTime now = LocalDateTime.now();
        Translate translate = Translate.getTranslateByRegion(region);
        transactionTemplate.execute((status) -> {
            log.info("开始填充{}地区{}游戏,名称:{}", region, app.getPackageName(), app.getTitle());
            try {
                String appSeoTitle = translate.getSeoTitle().replace("{appName}", app.getTitle());
                String appSeoKeywords = translate.getSeoKeywords().replace("{appName}", app.getTitle());
                String appSeoDesc = translate.getSeoDescription().replace("{appName}", app.getTitle()).replace("{shortDesc}", getShortDesc(app.getShortDesc()));
                // 先判断包名是否已存在
                Long postId = postMetaDAO.getPostIdByMetaKeyAndMetaValue("apkPackage", app.getPackageName());
                if (postId == null) {
                    String title = app.getTitle();
                    // 如果出现了-xxx的长标题，把后部分去掉，因为搜索结果不好，除非精准搜索，否则排名不靠前
                    title = getGameTitleByOriginTitle(title);
                    String gameExcerpt = translate.getAppShortDesc().replace("{appName}", title);
                    String postName = getRoutePackageNameByPackageName(app.getPackageName());
                    // 插入app
                    WPPost wpPost = WPPost.builder().postAuthor(10000L).postDate(now).postDateGmt(now)
                            .postContent("").postTitle(app.getTitle()).postExcerpt(gameExcerpt)
                            .postStatus("publish").commentStatus("closed").pingStatus("closed")
                            .postPassword("").postName(postName).toPing("").pinged("").postModified(now)
                            .postModifiedGmt(now).postContentFiltered("").postParent(0L)
                            .guid(translate.getDomainUrl() + postName + ".html").menuOrder(0)
                            .postType("game_management").postMimeType("").commentCount(0L).build();
                    gameDAO.insertWPPost(wpPost);
                    postId = wpPost.getId();
                    log.info("插入的postId:{},地区:{},游戏名:{},包名:{}", postId, region, app.getTitle(), app.getPackageName());
                    // 插入字段表  先判断是否存在  存在就更新 不存在则插入
                    insertOrUpdateAttributeByAppId(postId, "_seo_title", appSeoTitle);
                    insertOrUpdateAttributeByAppId(postId, "_seo_keywords", appSeoKeywords);
                    insertOrUpdateAttributeByAppId(postId, "_seo_description", appSeoDesc);
                    insertOrUpdateAttributeByAppId(postId, "gameGrade", handleScore(app.getScore()));
                    insertOrUpdateAttributeByAppId(postId, "gameVersion", StringUtils.isEmpty(app.getAppVersion()) ? "Varies with device" : app.getAppVersion());
                    insertOrUpdateAttributeByAppId(postId, "gamePeculiarity", app.getDetailHtml());
                    String ossLink = future1.join();
                    insertOrUpdateAttributeByAppId(postId, "gameIcon", ossLink);
                    insertOrUpdateAttributeByAppId(postId, "gameLogo", ossLink);
                    insertOrUpdateAttributeByAppId(postId, "gameVideo", getAppVideoLinkByOutAppAndRegion(region, app.getVideoLink()));
                    insertOrUpdateAttributeByAppId(postId, "gameVideoType", "other");
                    insertOrUpdateAttributeByAppId(postId, "downloadUrl", translate.getDownloadUrl().replace("{region}", region));
                    List<String> gamePhoto = future2.join();
                    insertOrUpdateAttributeByAppId(postId, "gamePhoto", gamePhoto.toString());
                    insertOrUpdateAttributeByAppId(postId, "gamePhotoType", getPhotoTypeByImageList(gamePhoto));
                    insertOrUpdateAttributeByAppId(postId, "isSimple", "false");
                    if ("jp".equals(region)) {
                        insertOrUpdateAttributeByAppId(postId, "gameBanner", SystemConstant.APPCENTER_JP_BANNER_URL);
                    } else {
                        insertOrUpdateAttributeByAppId(postId, "gameBanner", SystemConstant.APPCENTER_BANNER_URL);
                    }
                    insertOrUpdateAttributeByAppId(postId, "apkUrl", app.getGameLinkUrl());
                    insertOrUpdateAttributeByAppId(postId, "apkName", "APK");
                    insertOrUpdateAttributeByAppId(postId, "apkPackage", app.getPackageName());
                    insertOrUpdateAttributeByAppId(postId, "updateTime", stampConvert2Time(app.getGameUpdateTime()));
                    // 更新标签
                    insertOrUpdateLabelByAppId(region, app.getPackageName(), app.getTitle(), postId, getGameLabelByOriginLabel(app.getLabel()), "game_cate");
                    insertOrUpdateLabelByAppId(region, app.getPackageName(), app.getTitle(), postId, app.getGameType(), "game_tag");
                    log.info("填充{}地区{}游戏完成,名称:{}", region, app.getPackageName(), app.getTitle());
                } else {
                    log.info("{}地区{}游戏已存在", region, app.getPackageName());
                    // 运营有手动录入的游戏就不替换
                    GameDisplayRepVO game = gameDAO.getGameNameByGameId(postId);
                    if ((game != null && (!(game.getPostAuthor() == 10000L))) || region.equals("jp")) {
                        return null;
                    }
                    // 判断游戏信息是否一致  不一致更新
                    String gameIcon = postMetaDAO.getMetaValueByMetaKeyAndPostId(postId, "gameIcon");
                    String seoTitle = postMetaDAO.getMetaValueByMetaKeyAndPostId(postId, "_seo_title");
                    String seoDesc = postMetaDAO.getMetaValueByMetaKeyAndPostId(postId, "_seo_description");
                    String seoKeyWords = postMetaDAO.getMetaValueByMetaKeyAndPostId(postId, "_seo_keywords");
                    String gamePeculiarity = postMetaDAO.getMetaValueByMetaKeyAndPostId(postId, "gamePeculiarity");
                    String gameName = game.getGameName();
                    String packageName = game.getPackageName();
                    String ossUrl = future1.join();
                    if (!gameIcon.equals(ossUrl)) {
                        log.info("更新{}地区{}游戏的Icon", region, app.getPackageName());
                        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "gameIcon", ossUrl);
                        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "gameLogo", ossUrl);
                    }
                    if (!app.getTitle().equals(gameName)) {
                        log.info("更新{}地区{}游戏的名称,旧名称:{},新名称:{}", region, app.getPackageName(), gameName, app.getTitle());
                        gameDAO.updatePostTitleByPostId(postId, app.getTitle());
                    }
                    String newPackageName = getRoutePackageNameByPackageName(app.getPackageName());
                    if (!newPackageName.equals(packageName)) {
                        log.info("更新{}地区{}游戏的包名,旧包名:{},新包名:{}", region, app.getPackageName(), packageName, newPackageName);
                        gameDAO.updatePackageNameByPostId(postId, newPackageName);
                    }
                    if (!appSeoTitle.equals(seoTitle)) {
                        log.info("更新{}地区{}游戏的SeoTitle", region, app.getPackageName());
                        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "_seo_title", appSeoTitle);
                    }
                    if (!appSeoKeywords.equals(seoKeyWords)) {
                        log.info("更新{}地区{}游戏的SeoKeywords", region, app.getPackageName());
                        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "_seo_keywords", appSeoKeywords);
                    }
                    if (!appSeoDesc.equals(seoDesc)) {
                        log.info("更新{}地区{}游戏的SeoDesc", region, app.getPackageName());
                        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "_seo_description", appSeoDesc);
                    }
                    if (!gamePeculiarity.equals(app.getDetailHtml())) {
                        log.info("更新{}地区{}游戏的游戏介绍", region, app.getPackageName());
                        postMetaDAO.updateMetaValueByPostIdAndMetaKey(postId, "gamePeculiarity", app.getDetailHtml());
                    }
                }
                insertOrUpdateAttributeByAppId(postId, "downloadCount", StringUtils.isEmpty(app.getInstallCount()) ? "0" : app.getInstallCount());
                atomicInteger.incrementAndGet();
            } catch (Exception e) {
                // 手动回滚事务
                //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                status.setRollbackOnly();
                ThreadPoolTaskFuture.runAsync(() -> {
                    String msg = String.format("入库失败提醒,地区:%s,游戏包名:%s,游戏名称:%s,错误信息:%s", region, app.getPackageName(), app.getTitle(), ThrowableUtil.getStackTraceByPackage(e, "com.yeshen.appcenter"));
                    log.warn(msg, e);
                    DingTalkUtil.sendMessage(msg, true);
                });
            }
            return null;
        });
    }

    private static String handleScore(String score) {
        if (StringUtils.isEmpty(score)) {
            return "7";
        }
        if (score.compareTo("5.0") < 0) {
            return new BigDecimal(score).multiply(BigDecimal.valueOf(2L)).toString();
        } else {
            return score;
        }
    }


    /**
     * 时间戳转换为时间
     *
     * @param updateStamp
     * @return
     */
    private static String stampConvert2Time(Object updateStamp) {
        if (updateStamp instanceof String) {
            return (String) updateStamp;
        } else {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli((long) updateStamp), ZoneId.systemDefault()).toLocalDate().toString();
        }
    }

    /**
     * 截取长标题的前一部分
     *
     * @param title
     * @return
     */
    private static String getGameTitleByOriginTitle(String title) {
        if (title.length() > 0) {
            int index = title.indexOf("-");
            if (index != -1) {
                title = title.substring(0, index);
            }
        }
        return title;
    }

    /**
     * 截取长标签的前一部分
     *
     * @param label
     * @return
     */
    private static String getGameLabelByOriginLabel(String label) {
        if (label == null) {
            return label;
        }
        if (label.length() > 0) {
            if (label.startsWith("#")) {
                // 去掉#号  影响url定位
                label = label.substring(1);
            }
            int index = label.indexOf(" & ");
            if (index != -1) {
                label = label.substring(0, index);
            }
        }
        return label;
    }

    /**
     * 截取谷歌详情第一句话
     *
     * @param shortDesc
     * @return
     */
    private static String getShortDesc(String shortDesc) {
        if (shortDesc == null) {
            return "";
        }
        int index = shortDesc.indexOf(".");
        if (index == -1) {
            index = shortDesc.indexOf("。");
        }
        if (index == -1) {
            index = shortDesc.indexOf("!");
        }
        if (index != -1) {
            return shortDesc.substring(0, index + 1);
        } else {
            return shortDesc;
        }
    }


    /**
     * 包名转换成数据库中带后缀的postName
     *
     * @param packageName
     * @return
     */
    private static String getRoutePackageNameByPackageName(String packageName) {
        return packageName.replace(".", "-") + "-pc";
    }


    /**
     * 获取视频链接
     */
    private static String getAppVideoLinkByOutAppAndRegion(String region, String videoLink) {
        if (!StringUtils.isEmpty(videoLink)) {
            return "<iframe width=\"424\" height=\"238\" src=\"" + videoLink + "\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        }
        if ("cn".equals(region)) {
            return "<iframe src=\"//player.bilibili.com/player.html?aid=585838060&bvid=BV19z4y1r79a&cid=270398223&page=1\" scrolling=\"no\" border=\"0\" frameborder=\"no\" framespacing=\"0\" allowfullscreen=\"true\"> </iframe>";
        } else if ("jp".equals(region)) {
            return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/lEOwMU3_uPU\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        } else if ("tw".equals(region)) {
            return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/ZOmJxy0cGbA\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        } else if ("kr".equals(region)) {
            return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/iImBeIwGHY8\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        } else if ("th".equals(region)) {
            return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/cULoUWsVuLk\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        } else if ("vn".equals(region)) {
            return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/FNnhpvzogl4\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        } else {
            return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/KWsb9r9eZO8\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        }
    }

    /**
     * 多个游戏图片组成图片信息
     */
    private List<String> getOssGamePhotosByOutImageList(String region, String outImageList) {
        // 去掉前面后面的括号 依次上传到OSS
        String[] imageList = StringUtils.strip(outImageList, "[]").split(",");
        return getOssGamePhotosByOutImageList(region, Arrays.stream(imageList).collect(Collectors.toList()));
    }

    private List<String> getOssGamePhotosByOutImageList(String region, List<String> imageList) {
        ArrayList<String> result = new ArrayList<>(imageList.size());
        for (int i = 0; i < imageList.size(); i++) {
            // 注意取消前后空格
            String imageLink = StringUtils.trim(imageList.get(i));
            ImageInfo imageInfo = ossModule.uploadPhotoByUrl(region, imageLink);
            String gamePhoto = JSON.toJSONString(imageInfo, Labels.includes("wp"));
            // 图片去重
            if (!result.contains(gamePhoto)) {
                result.add(gamePhoto);
            }
        }
        return result;
    }

    /**
     * 获取图片类型(横屏还是竖屏)
     *
     * @param photoList
     * @return
     */
    private static String getPhotoTypeByImageList(List<String> photoList) {
        // 默认横屏
        String result = "horizontal";
        // 比较横屏竖屏图片个数
        int horizontalNumber = 0;
        int verticalNumber = 0;
        for (int i = 0; i < photoList.size(); i++) {
            String imageInfoJson = photoList.get(i);
            ImageInfo imageInfo = JSON.parseObject(imageInfoJson, ImageInfo.class);
            if (imageInfo.getWidth() > imageInfo.getHeight()) {
                horizontalNumber++;
            } else {
                verticalNumber++;
            }
        }
        if (verticalNumber > horizontalNumber) {
            result = "vertical";
        }
        return result;
    }

    /**
     * 插入或更新属性表的逻辑
     * 优化表结构可以使用INSERT INTO t1 (a,b,c) VALUES (1,2,3)  ON DUPLICATE KEY UPDATE c=c+1;
     *
     * @param appId
     * @param attributeName
     * @param attributeValue
     */
    private void insertOrUpdateAttributeByAppId(Long appId, String attributeName, String attributeValue) {
        if (StringUtils.isEmpty(attributeName)) {
            log.warn("metaKey:{},metaValue值异常:{},", attributeName, attributeValue);
            throw new BusinessException(ResultCode.WP_METAVALUE_ERROR);
        }
        String metaValue = postMetaDAO.getMetaValueByMetaKeyAndPostId(appId, attributeName);
        if (metaValue != null) {
            // 更新
            postMetaDAO.updateMetaValueByPostIdAndMetaKey(appId, attributeName, attributeValue);
        } else {
            // 插入
            postMetaDAO.insertPostMeta(appId, attributeName, attributeValue);
        }
    }

    /**
     * 插入标签表
     *
     * @param region
     * @param packageName
     * @param title
     * @param appId
     * @param label
     * @param labelType
     */
    private void insertOrUpdateLabelByAppId(String region, String packageName, String title, Long appId, String label, String labelType) {
        if (label == null) {
            return;
        }
        Long labelId = gameDAO.findLabelIdByName(label);
        // 如果是新标签的话则不插入标签表  通知钉钉
        if (labelId == null) {
            ThreadPoolTaskFuture.runAsync(() -> {
                String msg = String.format("新标签提醒,地区:%s,标签名:%s,游戏包名:%s,游戏名称:%s", region, label, packageName, title);
                log.warn(msg);
                DingTalkUtil.sendMessage(msg, false);
            });
            return;
        }
        Long relationId = gameDAO.findRelationIdByLabelId(labelId);
        if (relationId == null) {
            Long rootGameCate = 0L;
            if ("game_cate".equals(labelType)) {
                rootGameCate = gameDAO.findRootGameCateRelationId();
                if (rootGameCate == null) {
                    return;
                }
            }
            gameDAO.insertWPTermTaxonomy(labelId, labelType, rootGameCate);
            relationId = labelId;
        }
        if (gameDAO.existAppIdAndRelationId(appId, relationId) <= 0) {
            gameDAO.insertWPTermRelationships(appId, relationId);
        }
    }

    private void insertOrUpdateLabelByAppId(String region, String packageName, String title, Long appId, Integer gameType, String labelType) {
        if (gameType == 5) {
            // 预注册游戏
            insertOrUpdateLabelByAppId(region, packageName, title, appId, SystemConstant.PRE_REGISTRATION.get(region), labelType);
        } else if (gameType == 10) {
            // 热门免费应用
            insertOrUpdateLabelByAppId(region, packageName, title, appId, SystemConstant.POPULAR_APPLICATION.get(region), labelType);
        } else {
            // 热门游戏
            insertOrUpdateLabelByAppId(region, packageName, title, appId, SystemConstant.POPULAR_GAME.get(region), labelType);
        }
    }

    private static void killChromeDriverPID () {
        try {
            // 执行Shell命令，查询所有的ChromeDriver进程
            Process p1 = Runtime.getRuntime().exec("ps -ef | grep chromedriver");
            BufferedReader br1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            // 读取查询结果，并将每个进程的PID保存到一个数组中
            String line;
            String[] pidArr = new String[10];
            int i = 0;
            while ((line = br1.readLine()) != null) {
                String[] arr = line.split("\\s+");
                pidArr[i++] = arr[1];
            }
            br1.close();
            // 杀死所有的ChromeDriver进程
            for (String pid : pidArr) {
                if (pid != null) {
                    Process p2 = Runtime.getRuntime().exec("kill -9 " + pid);
                    p2.waitFor();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void CrawlingTaptapApp () {
        log.info("开始爬取taptap");
        ChromeDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(chromeOptions);
            // 获取网站截图
            // byte[] screenshotAs = driver.getScreenshotAs(OutputType.BYTES);
            // BufferedImage fullImg = ImageIO.read(new ByteArrayInputStream(screenshotAs));
            // System.out.println("图片width:"+fullImg.getWidth()+",height:"+fullImg.getHeight());
            // ImageIO.write(fullImg, "png", new File("./full.png"));
            // 添加请求头
            //driver.get("https://webdriver.modheader.com/add?accept-language=zh-CN");
            //Thread.sleep(1000);
            driver.get("https://www.taptap.com/top/download");
            // 打印日志
            //driver.manage().logs().get(LogType.PERFORMANCE).getAll().forEach(e -> System.out.println(e.getMessage()));
            checkPageTitle(driver.getTitle());
            // 滚轮滑到底部
            expendScroll(driver);
            List<WebElement> elements = driver.findElements(By.xpath("//div/div[contains(@class,'game-card__detail')]"));
            log.info("爬取数据大小:{}", elements.size());
            for (WebElement e : elements) {
                ReptileApp app = ReptileApp.getDefaultCNApp();
                LinkedList<String> imageList = new LinkedList<>();
                // 中间数据
                WebElement centerElement = e.findElement(By.cssSelector(".tap-row-card__contents"));
                WebElement titleElement = centerElement.findElement(By.cssSelector("p span"));
                // 设置游戏名称
                log.info("爬取当前游戏名称:{}", titleElement.getText());
                app.setTitle(titleElement.getText());
                // 设置包名
                app.setPackageName(ChineseCharacterHelper.getHeadPinyin(titleElement.getText()) + "-tt");
                WebElement scoreElement = centerElement.findElement(By.className("app-rating__number"));
                // 设置分数
                app.setScore(scoreElement.getText());
                WebElement tagElement = centerElement.findElement(By.className("label-tag-item"));
                // 设置标签
                app.setLabel(tagElement.findElement(By.className("tap-text")).getText());
                WebElement element = centerElement.findElement(By.className("tap-app-title__wrap"));
                String href = element.getAttribute("href");
                String js = "window.open(\"" + href + "\");";
                ((JavascriptExecutor) driver).executeScript(js);
                String windowHandle = driver.getWindowHandle();
                // 切换窗口到详情页
                Set<String> windowHandles = driver.getWindowHandles();
                for (String handle : windowHandles) {
                    if (!handle.equals(windowHandle)) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }
                // 非详情页直接跳过
                if (!driver.getCurrentUrl().contains("app")) {
                    driver.close();
                    driver.switchTo().window(windowHandle);
                    continue;
                }
                // 滑轮到底部
                expendScroll(driver);
                WebElement bannerElement = driver.findElement(By.xpath("//picture[contains(@class,'app-detail__cover tap-image-wrapper')]")).findElement(By.tagName("source"));
                // 设置banner图片
                app.setBannerUrl(bannerElement.getAttribute("srcset"));
                WebElement iconElement = driver.findElement(By.xpath("//picture[contains(@class,'header-banner__icon tap-image-wrapper')]")).findElement(By.tagName("source"));
                // 设置logo图片
                app.setLogoUrl(iconElement.getAttribute("srcset"));
                // 爬取游戏信息
                List<WebElement> detailElements = driver.findElements(By.cssSelector(".info-form__item"));
                for (WebElement webElement : detailElements) {
                    List<WebElement> spanList = webElement.findElements(By.tagName("span"));
                    if (spanList.size() == 2) {
                        String key = spanList.get(0).getText();
                        String value = spanList.get(1).getText();
                        if ("当前版本".equals(key)) {
                            app.setAppVersion(value);
                        } else if ("更新时间".equals(key)) {
                            app.setGameUpdateTime(value);
                        } else if ("系统版本要求".equals(key)) {
                            app.setMinAndroidRomVersion(value);
                        }
                    }
                }
                WebElement installElement = driver.findElement(By.xpath("//div/span[@class='game-info__stat--text'][1]/span[2]"));
                // 设置安装量
                app.setInstallCount(installElement.getText().replaceAll(",", ""));
                // 爬取图片集合
                // 点击图片切换按钮
                WebElement swiperButtonElement = driver.findElement(By.xpath("//div[contains(@class,'tap-swiper-button--image')][@aria-label='Next slide']"));
                while (swiperButtonElement.isDisplayed()) {
                    new Actions(driver).click(swiperButtonElement).perform();
                    Thread.sleep(500);
                }
                List<WebElement> imgElements = driver.findElements(By.xpath("//section[contains(@class,'app-info-intro app-detail__section-card app-detail__warp')]/div/div/div[@class='swiper-container swiper swiper-container-initialized swiper-container-horizontal']/div[@class='swiper-wrapper']/div"));
                for (WebElement imgElement : imgElements) {
                    WebElement picture;
                    try {
                        picture = imgElement.findElement(By.tagName("picture"));
                        if (picture != null) {
                            imageList.add(picture.findElement(By.tagName("source")).getAttribute("srcset"));
                        }
                    } catch (org.openqa.selenium.NoSuchElementException exception) {
                        log.warn("找不到picture标签");
                    }
                }
                // 设置图片集合
                app.setImageUrls(imageList.toString());
                // 如果有展开按钮
                try {
                    WebElement openElement = driver.findElement(By.xpath("//span[@data-track-prevent='click']"));
                    new Actions(driver).click(openElement).perform();
                    WebElement descElement = driver.findElement(By.cssSelector(".text-box.text-box__text-overflow"));
                    app.setDetailHtml(handleSensitiveWord(descElement.getAttribute("text")));
                } catch (Exception exception) {
                    log.info("找不到展开按钮,网页名称:{}", driver.getTitle());
                    WebElement descElement = driver.findElement(By.xpath("//section[contains(@class,'app-info-summary app-detail__section-card app-detail__warp')]/main/div[@class='text-box']"));
                    app.setDetailHtml(handleSensitiveWord(descElement.getAttribute("text")));
                }
                log.info("游戏详情:{}", app);
                if (!gameNameIsExist(app.getTitle())) {
                    CompletableFuture<String> future1 = ThreadPoolTaskFuture.supplyAsync(() -> ossModule.uploadPhotoByUrl("cn", app.getLogoUrl()).getOssUrl());
                    CompletableFuture<List<String>> future2 = ThreadPoolTaskFuture.supplyAsync(() -> getOssGamePhotosByOutImageList("cn", imageList));
                    fillWordpressDbByApp(app, SupportDatasourceEnum.PRD_CN, new AtomicInteger(), future1, future2);
                }
                // 关闭窗口  切换到榜单页
                driver.close();
                driver.switchTo().window(windowHandle);
            }
        } catch (Exception exception) {
            String stackTrace = ThrowableUtil.getStackTraceByPackage(exception, "com.yeshen.appcenter");
            log.warn("爬取失败提醒,网站名:{},网站链接:{},异常类:{},原因:{}", driver.getTitle(), driver.getCurrentUrl(), exception.getClass(), stackTrace, exception);
            WebDriver finalDriver = driver;
            ThreadPoolTaskFuture.runAsync(() -> {
                String msg = String.format("爬取失败提醒,抓取地区:%s\n,网站名:%s\n,网站链接:%s\n,异常信息:%s", "cn", finalDriver.getTitle(), finalDriver.getCurrentUrl(), stackTrace);
                DingTalkUtil.sendMessage(msg, true);
            });
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void expendScroll(WebDriver driver) throws InterruptedException {
        Thread.sleep(1000);
        long nowHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
        while (true) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight)");
            Thread.sleep(1000);
            long totalHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
            if (nowHeight != totalHeight) {
                nowHeight = totalHeight;
            } else {
                break;
            }
        }
    }

    private String handleSensitiveWord(String desc) {
        if (desc == null) {
            return "";
        }
        return desc.replaceAll("<a href.*?/a>", "");
    }

    private String handleBannerImgUrl(String imgUrl) {
        String regx = "url[(]\".*?\"";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(imgUrl);
        String result = null;
        while (m.find()) {
            String group = m.group();
            group = group.replace("url(\"", "");
            result = group.replace("\"", "");
            break;
        }
        return result;
    }

    private void checkPageTitle(String title) {
        if (!"安卓总榜热门榜 | TapTap".equals(title)) {
            log.warn("爬取taptap网站错误,title:{}", title);
            ThreadPoolTaskFuture.runAsync(() -> {
                String msg = String.format("网站错误提醒,抓取地区:%s,taptap网页抓取错误,网页名称:%s", "cn", title);
                DingTalkUtil.sendMessage(msg, true);
            });
        }
    }

    private boolean gameNameIsExist(String gameName) {
        return gameDAO.gameNameIsExist(gameName) >= 1;
    }
}