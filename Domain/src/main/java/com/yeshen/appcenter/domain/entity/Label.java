package com.yeshen.appcenter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * author YangGuo
 * Date 2022/2/9
 */
@Data
@TableName("wp_terms")
public class Label {
    @TableId("term_id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField("slug")
    private String slug;
}