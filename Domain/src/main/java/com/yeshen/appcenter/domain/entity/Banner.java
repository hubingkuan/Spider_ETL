package com.yeshen.appcenter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
@Data
@TableName("wp_banner")
public class Banner {
    @TableId
    private Integer id;

    /**
     * banner图片关联的游戏id
     */
    @TableField("post_id")
    private Integer postId;

    /**
     * banner图片的超链接
     */
    @TableField("banner")
    private String bannerImageUrl;

    @TableField("time")
    private LocalDateTime time;
}