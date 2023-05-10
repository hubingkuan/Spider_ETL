package com.yeshen.appcenter.utils;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.entity.PostMeta;
import com.yeshen.appcenter.domain.vo.response.GameDetailRepVO;

import java.util.List;

/**
 * Date 2022/1/25/0025
 * author by HuBingKuan
 */
public class AttributeToGameUtil {
    public static GameDetailRepVO attributeToGameDetail(List<PostMeta> attributes) {
        GameDetailRepVO gameDetail = new GameDetailRepVO();
        attributes.forEach(e -> {
            String metaKey = e.getMetaKey();
            String metaValue = e.getMetaValue();
            switch (metaKey) {
                case SystemConstant.GAMEBANNER:
                    gameDetail.setGameBanner(metaValue);
                    break;
                case SystemConstant.GAMEICON:
                    gameDetail.setGameIcon(metaValue);
                    break;
                case SystemConstant.GAMENAME:
                    gameDetail.setGameName(metaValue);
                    break;
                case SystemConstant.GAMEGRADE:
                    gameDetail.setGameGrade(metaValue);
                    break;
                case SystemConstant.UPDATETIME:
                    gameDetail.setUpdateTime(metaValue);
                    break;
                case SystemConstant.GAMEVERSION:
                    gameDetail.setGameVersion(metaValue);
                    break;
                case SystemConstant.DOWNLOADCOUNT:
                    gameDetail.setDownloadCount(metaValue);
                    break;
                case SystemConstant.DOWNLOADURL:
                    gameDetail.setDownloadUrl(metaValue);
                    break;
                case SystemConstant.APKURL:
                    gameDetail.setApkUrl(metaValue);
                    break;
                case SystemConstant.APKNAME:
                    gameDetail.setApkName(metaValue);
                    break;
                case SystemConstant.GAMEPECULIARITY:
                    gameDetail.setGamePeculiarity(metaValue);
                    break;
                case SystemConstant.GAMEPHOTO:
                    gameDetail.setGamePhoto(metaValue);
                    break;
                case SystemConstant.GAMEVIDEO:
                    gameDetail.setGameVideo(metaValue);
                    break;
                case SystemConstant.APKPACKAGE:
                    gameDetail.setApkPackage(metaValue);
                default:
            }
        });
        return gameDetail;
    }
}