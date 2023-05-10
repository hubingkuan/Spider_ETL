package com.yeshen.appcenter.config.mysql;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Date 2022/04/02  20:54
 * author by HuBingKuan
 */
@Slf4j
public class DataSourceContextHolder {
    private static final List<SupportDatasourceEnum> DATASOURCE_SET = new ArrayList<>();

    /**
     * TransmittableThreadLocal  继承了InheritableThreadLocal
     * 会让子线程继承父线程的ThreadLocal值 同时还会让线程池中的也同步
     */
    private static final ThreadLocal<String> DATABASE_HOLDER = new TransmittableThreadLocal();

    public static void setDatabaseHolder(SupportDatasourceEnum supportDatasourceEnum) {
        clear();
        log.info("使用数据源:{}", supportDatasourceEnum);
        DATABASE_HOLDER.set(supportDatasourceEnum.name());
    }

    /**
     * 取得当前数据源
     *
     * @return
     */
    public static String getDatabaseHolder() {
        return DATABASE_HOLDER.get();
    }

    /**
     * 添加数据源
     *
     * @param supportDatasourceEnum
     */
    public static void addDatasource(SupportDatasourceEnum supportDatasourceEnum) {
        DATASOURCE_SET.add(supportDatasourceEnum);
    }

    /**
     * 获取当期应用所支持的所有数据源
     *
     * @return
     */
    public static List<SupportDatasourceEnum> getDataSourceList() {
        return DATASOURCE_SET;
    }

    /**
     * 清除上下文数据
     */
    public static void clear() {
        DATABASE_HOLDER.remove();
    }
}