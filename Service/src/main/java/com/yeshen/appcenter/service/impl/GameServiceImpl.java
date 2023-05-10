package com.yeshen.appcenter.service.impl;


import com.yeshen.appcenter.config.executor.ThreadPoolTaskFuture;
import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.entity.Label;
import com.yeshen.appcenter.domain.entity.PostMeta;
import com.yeshen.appcenter.domain.enums.ResultCode;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import com.yeshen.appcenter.domain.vo.request.GameSearchQueryVO;
import com.yeshen.appcenter.domain.vo.request.LabelGamesListReqVO;
import com.yeshen.appcenter.domain.vo.response.*;
import com.yeshen.appcenter.repository.mysql.BannerDAO;
import com.yeshen.appcenter.repository.mysql.GameDAO;
import com.yeshen.appcenter.repository.mysql.PostMetaDAO;
import com.yeshen.appcenter.service.GameService;
import com.yeshen.appcenter.service.LabelService;
import com.yeshen.appcenter.utils.AttributeToGameUtil;
import com.yeshen.appcenter.utils.ThrowableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * author by HuBingKuan
 * Date 2022/1/18/0018
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameDAO gameDao;

    private final BannerDAO bannerDao;

    private final PostMetaDAO postMetaDao;

    private final LabelService labelService;

    private List getImagesAndGradeAndTypes(List<Long> postIds) {
        CompletableFuture work1 = ThreadPoolTaskFuture.supplyAsync(() -> postMetaDao.getPostMetaList(postIds, SystemConstant.GAMEICON));
        CompletableFuture work2 = ThreadPoolTaskFuture.supplyAsync(() -> postMetaDao.getPostMetaList(postIds, SystemConstant.GAMEGRADE));
        CompletableFuture work3 = ThreadPoolTaskFuture.supplyAsync(() -> gameDao.getTypeList(postIds));
        Map<Long, PostMeta> images = (Map<Long, PostMeta>) work1.join();
        Map<Long, PostMeta> grades = (Map<Long, PostMeta>) work2.join();
        Map<Long, TypeVO> types = (Map<Long, TypeVO>) work3.join();
        List<Map<Long, ? extends Object>> result = new ArrayList<>();
        result.add(images);
        result.add(grades);
        result.add(types);
        return result;
    }

    @Override
    public Map<String,Object> searchGame(GameSearchQueryVO query) {
        String dataSourceName = DataSourceContextHolder.getDatabaseHolder();
        String keywords = query.getKeywords();
        Integer pageSize = query.getPageSize();
        int startIndex = (query.getPageNum() - 1) * pageSize;
        HashMap<String, Object> result = new HashMap<>(2);
        List<GameDisplayRepVO> games;
        if (SupportDatasourceEnum.checkSupportFullText(dataSourceName)) {
            games = gameDao.getGamesWithGradeAndImageByNaturalLanguageSearch(query.getKeywords(), startIndex, pageSize);
        } else {
            games = gameDao.getGamesWithGradeAndImageByKeywords(keywords, startIndex, pageSize);
        }
        result.put("records",games);
        result.put("totalCount",games.size());
        return result;
    }


    @Override
    @Cacheable(key = "#prefix", value = "game_download_top")
    public List<GameDisplayRepVO> getGameDownloadTopTenDao(String prefix) {
        List<GameDisplayRepVO> gameList = gameDao.getGameDownloadTopTen();
        List<Long> postIds = new ArrayList<>();
        for (GameDisplayRepVO game : gameList) {
            postIds.add(game.getId());
        }
        List imagesAndGradeAndTypes = getImagesAndGradeAndTypes(postIds);
        Map<Long, PostMeta> images = (Map<Long, PostMeta>) imagesAndGradeAndTypes.get(0);
        Map<Long, PostMeta> gameGrade = (Map<Long, PostMeta>) imagesAndGradeAndTypes.get(1);
        Map<Long, TypeVO> types = (Map<Long, TypeVO>) imagesAndGradeAndTypes.get(2);
        for (int i = 0; i < gameList.size(); i++) {
            GameDisplayRepVO game = gameList.get(i);
            PostMeta image = images.get(game.getId());
            if (image != null) {
                game.setImgUrl(image.getMetaValue());
            }
            PostMeta grade = gameGrade.get(game.getId());
            if (grade != null) {
                game.setGameGrade(grade.getMetaValue());
            }
            TypeVO type = types.get(game.getId());
            if (type != null) {
                game.setType(type.getType());
            }
        }
        return gameList;
    }


    @Override
    @Cacheable(key = "#prefix", value = "all_banner")
    public List<BannerVO> getBannerDao(String prefix) {
        List<BannerVO> bannerList = bannerDao.getBannerList();
        List<Long> postIds = new ArrayList<>();
        for (BannerVO banner : bannerList) {
            postIds.add(banner.getGameId());
        }
        List imagesAndGradeAndTypes = getImagesAndGradeAndTypes(postIds);
        Map<Long, PostMeta> images = (Map<Long, PostMeta>) imagesAndGradeAndTypes.get(0);
        Map<Long, PostMeta> gameGrade = (Map<Long, PostMeta>) imagesAndGradeAndTypes.get(1);
        Map<Long, TypeVO> types = (Map<Long, TypeVO>) imagesAndGradeAndTypes.get(2);
        for (int i = 0; i < bannerList.size(); i++) {
            BannerVO banner = bannerList.get(i);
            PostMeta image = images.get(banner.getGameId());
            if (image != null) {
                banner.setImageUrl(image.getMetaValue());
            }
            PostMeta grade = gameGrade.get(banner.getGameId());
            if (grade != null) {
                banner.setGameGrade(grade.getMetaValue());
            }
            TypeVO type = types.get(banner.getGameId());
            if (type != null) {
                banner.setType(type.getType());
            }
        }
        return bannerList;
    }

    @Override
    @Cacheable(key = "#prefix", value = "recommend_game")
    public IndexGameDisplayListRepVO getRecommendGameDao(String prefix) {
        String dataSourceName = DataSourceContextHolder.getDatabaseHolder();
        String region = dataSourceName.split("_")[1].toLowerCase();
        String preRegistration = SystemConstant.PRE_REGISTRATION.get(region);
        String popularGames = SystemConstant.POPULAR_GAME.get(region);
        String popularApplications = SystemConstant.POPULAR_APPLICATION.get(region);
        CompletableFuture<List<GameDisplayRepVO>> future1 = ThreadPoolTaskFuture.supplyAsync(() ->
                gameDao.getRecommendGamesWithGradeAndImage(preRegistration, 12));
        CompletableFuture<List<GameDisplayRepVO>> future2 = ThreadPoolTaskFuture.supplyAsync(() ->
                gameDao.getRecommendGamesWithGradeAndImage(popularGames, 24));
        CompletableFuture<List<GameDisplayRepVO>> future3 = ThreadPoolTaskFuture.supplyAsync(() ->
                gameDao.getRecommendGamesWithGradeAndImage(popularApplications, 24));
        CompletableFuture.allOf(future1, future2, future3);
        return new IndexGameDisplayListRepVO().setPreRegistrations(future1.join())
                .setPopularGames(future2.join())
                .setPopularApplications(future3.join());
    }

    @Override
    public LabelGameDisplayListRepVO getGameAndBannerByLabel(String labelName) {
        return ThreadPoolTaskFuture.supplyAsync(() -> gameDao.getGamesWithGradeAndImageByLabel(labelName, 0, SystemConstant.INIT_PAGESIZE))
                .thenApply(list -> {
                    if (CollectionUtils.isEmpty(list)) {
                        return new LabelGameDisplayListRepVO().setGames(Collections.emptyList()).setBannerImgUrl(SystemConstant.APPCENTER_BANNER_URL);
                    } else {
                        Optional<String> bannerUrl = Optional.ofNullable(gameDao.getBannerImageUrlByGameId(list.get(0).getId()));
                        return new LabelGameDisplayListRepVO().setGames(list).setBannerImgUrl(bannerUrl.orElse(SystemConstant.APPCENTER_BANNER_URL));
                    }
                }).exceptionally(e -> {
                    log.error("GameService.getGameAndBannerByLabel Exception labelName={}", labelName, ThrowableUtil.extractRealException(e));
                    return new LabelGameDisplayListRepVO();
                }).join();
    }

    @Override
    public List<GameDisplayRepVO> getGameByLabel(LabelGamesListReqVO req) {
        int startIndex = (req.getPageNum() - 1) * req.getPageSize();
        return gameDao.getGamesWithGradeAndImageByLabel(req.getLabelName(), startIndex, req.getPageSize());
    }


    @Override
    public GameDetailRepVO getGameDetailByPostName(String postName) {
        CompletableFuture<GameDisplayRepVO> c1 = ThreadPoolTaskFuture.supplyAsync(() -> gameDao.getIdByPostName(postName));
        CompletableFuture<List<PostMeta>> c2 = c1.thenApply((game) -> {
            if (game == null) {
                log.warn("locale:{},查询不到该包名游戏:{}",DataSourceContextHolder.getDatabaseHolder(),postName);
                throw new BusinessException(ResultCode.GAME_NOT_EXIST);
            }
            return gameDao.getAttributeByGameId(game.getId());
        });
        CompletableFuture<String> c3 = ThreadPoolTaskFuture.supplyAsync(() -> getWebSiteTitle(DataSourceContextHolder.getDatabaseHolder()));
        return AttributeToGameUtil.attributeToGameDetail(c2.join())
                .setGameName(c1.join().getGameName())
                .setExcerpt(c1.join().getExcerpt())
                .setPageTitle(c1.join().getGameName()+c3.join());
    }

    @Override
    public List<GameDisplayRepVO> getBottomGameNotInGameId(String postName) {
        return ThreadPoolTaskFuture.supplyAsync(() -> gameDao.getIdByPostName(postName))
                .thenApply((game) -> {
                    log.warn("locale:{},查询不到该包名游戏:{}",DataSourceContextHolder.getDatabaseHolder(),postName);
                    if (game == null) {
                        throw new BusinessException(ResultCode.GAME_NOT_EXIST);
                    }
                    return gameDao.getBottomGameNotInGameId(game.getId());
                }).join();
    }

    @Override
    public List<XMLVO> getSiteMapInfo(String prefixLocation) {
        StringBuilder builder = new StringBuilder();
        LocalDate now = LocalDate.now();
        List<XMLVO> result = new LinkedList<>();
        result.add(new XMLVO(prefixLocation,now));
        List<Label> labelList = labelService.getDistinctLabelsByParentIdDao(DataSourceContextHolder.getDatabaseHolder());
        int labelSize = labelList.size();
        for (int i = 0; i <labelSize ; i++) {
            Label source = labelList.get(i);
            XMLVO target = new XMLVO();
            builder.append(prefixLocation).append(SystemConstant.PREFIX_LABEL).append(SystemConstant.URI_SEPARATOR)
                    .append(source.getSlug()).append(SystemConstant.URI_SEPARATOR)
                    .append(source.getId());
            target.setLocation(builder.toString());
            target.setLocalDate(now);
            result.add(target);
            builder.setLength(0);
        }
        List<GameDisplayRepVO> gameLists = gameDao.getAllEffectivePackageNames();
        int gameSize = gameLists.size();
        for (int i = 0; i <gameSize ; i++) {
            GameDisplayRepVO source = gameLists.get(i);
            XMLVO target = new XMLVO();
            builder.append(prefixLocation).append(source.getPackageName()).append(SystemConstant.SUFFIX_LOCATION);
            target.setLocation(builder.toString());
            target.setLocalDate(source.getLastModTime().toLocalDate());
            result.add(target);
            builder.setLength(0);
        }
        return  result;
    }

    @Override
    @Cacheable(key = "#prefix", value = "webSiteTitleSuffix")
    public String getWebSiteTitle(String prefix) {
        return gameDao.getWebSiteTitle();
    }
}