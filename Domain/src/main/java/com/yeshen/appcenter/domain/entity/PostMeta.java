package com.yeshen.appcenter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * Date 2022/1/19 19:26
 * author by YangGuo
 */
@Data
@TableName("wp_postmeta")
public class PostMeta {
    @TableId
    private Long metaId;
    private Long postId;
    private String metaKey;
    private String metaValue;
}