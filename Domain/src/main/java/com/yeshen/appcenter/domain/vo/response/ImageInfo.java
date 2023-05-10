package com.yeshen.appcenter.domain.vo.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date 2022/07/13  13:56
 * author  by HuBingKuan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfo {
    private String gpUrl;
    @JSONField(ordinal = 1,alternateNames = "src",name = "src",label = "wp")
    private String ossUrl;
    @JSONField(ordinal = 2,label = "wp")
    private Integer width;
    @JSONField(ordinal = 3,label = "wp")
    private Integer height;
}