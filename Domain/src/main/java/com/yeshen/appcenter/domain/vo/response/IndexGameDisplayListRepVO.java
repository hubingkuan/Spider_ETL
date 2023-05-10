package com.yeshen.appcenter.domain.vo.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 */
@Data
@Accessors(chain = true)
public class IndexGameDisplayListRepVO {
    private List<GameDisplayRepVO> preRegistrations;
    private List<GameDisplayRepVO> popularGames;
    private List<GameDisplayRepVO> popularApplications;
}