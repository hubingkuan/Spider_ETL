package com.yeshen.appcenter.service;

import com.yeshen.appcenter.domain.vo.request.GameSearchQueryVO;
import com.yeshen.appcenter.domain.vo.request.LabelGamesListReqVO;
import com.yeshen.appcenter.domain.vo.response.*;

import java.util.List;
import java.util.Map;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public interface GameService {
    /**
     * 根据关键字搜索游戏列表
     *
     * @param query
     * @return
     */
    Map<String,Object> searchGame(GameSearchQueryVO query);

    /**
     * 获取当前地区banner列表
     *
     * @return
     */
    List<BannerVO> getBannerDao(String prefix);

    /**
     * 获取当前游戏下载排行榜前十
     *
     * @return
     */
    List<GameDisplayRepVO> getGameDownloadTopTenDao(String prefix);

    /**
     * 获取推荐游戏
     *
     * @return
     */
    IndexGameDisplayListRepVO getRecommendGameDao(String prefix);

    /**
     * 按标签名的slug获取对应的游戏和banner
     *
     * @param labelName
     * @return
     */
    LabelGameDisplayListRepVO getGameAndBannerByLabel(String labelName);

    /**
     * 按标签获取更多游戏，分页
     *
     * @param reqVO
     * @return
     */
    List<GameDisplayRepVO> getGameByLabel(LabelGamesListReqVO reqVO);

    /**
     * 通过游戏名包格式化获取游戏详情
     *
     * @param postName
     * @return
     */
    GameDetailRepVO getGameDetailByPostName(String postName);

    /**
     * 获取底部游戏,排除部分ID
     *
     * @param postName
     * @return
     */
    List<GameDisplayRepVO> getBottomGameNotInGameId(String postName);

    /**
     * 获取所有正常状态游戏的链接
     */
    List<XMLVO> getSiteMapInfo(String prefixLocation);

    /**
     * 获取网站标题后缀
     */
    String getWebSiteTitle(String prefix);
}