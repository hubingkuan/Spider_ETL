package com.yeshen.appcenter.domain.vo.response;

import lombok.Data;

/**
 * Date 2022/1/19 20:05
 * author YangGuo
 */
@Data
public class BannerVO {
    /**
     * 游戏Id
     */
    private Long gameId;
    /**
     * Banner图片连接
     */
    private String bannerUrl;
    /**
     * 游戏图表连接(Icon)
     */
    private String imageUrl;
    /**
     * 游戏名字
     */
    private String gameName;
    /**
     * 返回游戏对应的postName，用于SEO
     */
    private String packageName;
    /**
     * 游戏的类型
     */
    private String type;
    /**
     * 星级
     */
    private String gameGrade;
}