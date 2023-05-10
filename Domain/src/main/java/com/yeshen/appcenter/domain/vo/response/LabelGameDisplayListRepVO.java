package com.yeshen.appcenter.domain.vo.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Date 2022/1/24/0024
 * author by HuBingKuan
 */
@Data
@Accessors(chain = true)
public class LabelGameDisplayListRepVO {
    String bannerImgUrl;
    List<GameDisplayRepVO> games;
}