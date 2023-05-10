package com.yeshen.appcenter.domain.vo.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 */
@Data
@Accessors(chain = true)
public class GameDetailRepVO {
    private String gameBanner;
    private String gameIcon;
    private String gameName;
    private String gameGrade;
    private String updateTime;
    private String gameVersion;
    private String downloadCount;
    private String downloadUrl;
    private String apkUrl;
    private String apkName;
    private String gamePeculiarity;
    private String gamePhoto;
    private String gameVideo;
    private String excerpt;
    private String pageTitle;
    private String apkPackage;
}