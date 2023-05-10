package com.yeshen.appcenter.domain.vo.response;

import lombok.Data;
/**
 * Date 2022/1/24 16:21
 * author by YangGuo
 */
@Data
public class TypeVO {
    /**
     * 保存一个Post对应的类型
     */
    private Long postId;
    private String type;
}