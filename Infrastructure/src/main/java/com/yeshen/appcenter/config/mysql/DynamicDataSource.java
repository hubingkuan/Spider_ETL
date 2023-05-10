package com.yeshen.appcenter.config.mysql;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Date 2022/04/02  20:57
 * author by HuBingKuan
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDatabaseHolder();
    }
}