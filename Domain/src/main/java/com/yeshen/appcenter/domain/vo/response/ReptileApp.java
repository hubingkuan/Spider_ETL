package com.yeshen.appcenter.domain.vo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Date 2022/07/08  16:40
 * author  by HuBingKuan
 */
@Getter
@Setter
public class ReptileApp {
    private Long id;
    private String title;
    private String packageName;
    private String logoUrl;
    private String gameLinkUrl;
    /**
     * 爬虫库中 list-type与list-title的对应关系
     * 1-热门免费-游戏   topSellingGame
     * 2-创收最高-游戏   topGrossingGame
     * 3-热门付费-游戏   topSellingPaid
     * 4-热门游戏       recommendedPopularGame
     * 5-预注册-游戏     preRegistrationGame
     * 6-休闲游戏
     * 10-热门免费应用   topSellingApp
     */
    private Integer gameType;
    private String score;
    private Object gameUpdateTime;
    private String imageUrls;
    private String shortDesc;
    private String detail;
    private String detailHtml;
    private String appVersion;
    private String minAndroidRomVersion;
    private String videoLink;
    private String label;
    private String installCount;
    private String bannerUrl;

    public static ReptileApp getDefaultCNApp(){
        ReptileApp reptileApp = new ReptileApp();
        reptileApp.setGameType(1);
        reptileApp.setGameLinkUrl("https://app.yeshen.com/");
        return reptileApp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ReptileApp that = (ReptileApp) o;
        return Objects.equals(title, that.title) && Objects.equals(packageName, that.packageName) && Objects.equals(logoUrl, that.logoUrl) && Objects.equals(gameLinkUrl, that.gameLinkUrl) && Objects.equals(gameType, that.gameType) && Objects.equals(score, that.score) && Objects.equals(imageUrls, that.imageUrls) && Objects.equals(shortDesc, that.shortDesc) && Objects.equals(detail, that.detail) && Objects.equals(detailHtml, that.detailHtml) && Objects.equals(appVersion, that.appVersion) && Objects.equals(minAndroidRomVersion, that.minAndroidRomVersion) && Objects.equals(videoLink, that.videoLink) && Objects.equals(label, that.label) && Objects.equals(installCount, that.installCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, packageName, logoUrl, gameLinkUrl, gameType, score, imageUrls, shortDesc, detail, detailHtml, appVersion, minAndroidRomVersion, videoLink, label, installCount);
    }

    @Override
    public String toString() {
        return "ReptileApp{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", packageName='" + packageName + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", gameLinkUrl='" + gameLinkUrl + '\'' +
                ", gameType=" + gameType +
                ", score='" + score + '\'' +
                ", gameUpdateTime=" + gameUpdateTime +
                ", imageUrls='" + imageUrls + '\'' +
                ", shortDesc='" + shortDesc + '\'' +
                ", detail='" + detail + '\'' +
                ", detailHtml='" + detailHtml + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", minAndroidRomVersion='" + minAndroidRomVersion + '\'' +
                ", videoLink='" + videoLink + '\'' +
                ", label='" + label + '\'' +
                ", installCount='" + installCount + '\'' +
                ", bannerUrl='" + bannerUrl + '\'' +
                '}';
    }
}