package com.yeshen.appcenter.domain.vo.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 */
@Data
public class GameDisplayRepVO {
    private Long id;

    /**
     * 录入游戏的操作者
     */
    private Long postAuthor;

    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 返回游戏对应的postName，用于SEO
     */
    private String packageName;
    /**
     * 图片链接
     */
    private String imgUrl;
    /**
     * 游戏评分
     */
    private String gameGrade;
    /**
     * 游戏类型
     */
    private String type;
    /**
     * 下载量
     */
    private Long downCount;

    /**
     * 游戏简介
     */
    private String excerpt;

    /**
     * 更新时间
     */
    private LocalDateTime lastModTime;
}