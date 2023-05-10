package com.yeshen.appcenter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Date 2022/07/11  20:41
 * author  by HuBingKuan
 */
@Data
@Builder
@TableName("wp_posts")
public class WPPost {
    @TableId(value = "ID")
    private Long id;

    @TableField("post_author")
    private Long postAuthor;

    @TableField("post_date")
    private LocalDateTime postDate;

    @TableField("post_date_gmt")
    private LocalDateTime postDateGmt;

    @TableField("post_content")
    private String postContent;

    @TableField("post_title")
    private String postTitle;

    @TableField("post_excerpt")
    private String postExcerpt;

    @TableField("post_status")
    private String postStatus;

    @TableField("comment_status")
    private String commentStatus;

    @TableField("ping_status")
    private String pingStatus;

    @TableField("post_password")
    private String postPassword;

    @TableField("post_name")
    private String postName;

    @TableField("to_ping")
    private String toPing;

    @TableField("pinged")
    private String pinged;

    @TableField("post_modified")
    private LocalDateTime postModified;

    @TableField("post_modified_gmt")
    private LocalDateTime postModifiedGmt;

    @TableField("post_content_filtered")
    private String postContentFiltered;

    @TableField("post_parent")
    private Long postParent;

    @TableField("guid")
    private String guid;

    @TableField("menu_order")
    private Integer menuOrder;

    @TableField("post_type")
    private String postType;

    @TableField("post_mime_type")
    private String postMimeType;

    @TableField("comment_count")
    private Long commentCount;

    public static WPPost buildTWBasePost(){
        return WPPost.builder().postAuthor(1L)
                .postDate(LocalDateTime.now())
                .postDateGmt(LocalDateTime.now())
                .postExcerpt("")
                .postStatus("publish").commentStatus("closed").pingStatus("open")
                .postPassword("").toPing("").pinged("").postModified(LocalDateTime.now())
                .postModifiedGmt(LocalDateTime.now())
                .postContentFiltered("").postParent(0L)
                .menuOrder(0)
                .postType("post").postMimeType("").commentCount(0L).build();
    }

    public static WPPost buildUSBasePost(){
        return WPPost.builder().postAuthor(1L)
                .postDate(LocalDateTime.now())
                .postDateGmt(LocalDateTime.now())
                .postExcerpt("")
                .postStatus("publish").commentStatus("closed").pingStatus("open")
                .postPassword("").toPing("").pinged("").postModified(LocalDateTime.now())
                .postModifiedGmt(LocalDateTime.now())
                .postContentFiltered("").postParent(0L)
                .menuOrder(0)
                .postType("post").postMimeType("").commentCount(0L).build();
    }
}