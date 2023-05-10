package com.yeshen.appcenter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * Date 2022/07/14  21:10
 * author  by HuBingKuan
 */
@Data
@Builder
@TableName("wp_terms")
public class Terms {
    @TableId
    private Long termId;
    private String name;
    private String slug;
    private Long termGroupId;
}