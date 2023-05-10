package com.yeshen.appcenter.service.strategy;

import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.enums.ResultCode;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Date 2023/01/11  17:41
 * author  by HuBingKuan
 */
@Slf4j
public class HandlerContentStrategyContext {
    private static final Map<SupportDatasourceEnum, HandlerPageContentStrategy> registerMap = new HashMap<>();

    // 注册策略
    public static void registerStrategy(SupportDatasourceEnum datasourceEnum, HandlerPageContentStrategy strategy) {
        registerMap.putIfAbsent(datasourceEnum, strategy);
    }
    // 获取策略
    public static HandlerPageContentStrategy getStrategy(SupportDatasourceEnum datasourceEnum) {
        HandlerPageContentStrategy strategy = registerMap.get(datasourceEnum);
        if(strategy==null){
            log.error("没有对应数据源的处理文本内容策略类");
            throw new BusinessException(ResultCode.SERVER_ERROR);
        }
        return strategy;
    }
}