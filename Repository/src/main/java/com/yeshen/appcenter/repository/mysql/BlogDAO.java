package com.yeshen.appcenter.repository.mysql;

import com.yeshen.appcenter.domain.vo.response.GameDisplayRepVO;

import java.util.List;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public interface BlogDAO{
    List<GameDisplayRepVO>  getBlogLocations();
}