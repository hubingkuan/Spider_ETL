package com.yeshen.appcenter.aop;

import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Date 2022/04/03  16:23
 * author by HuBingKuan
 */
@Slf4j
@Aspect
@Order(-1)
@Component
public class DataSourceAspect {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    private static String defaultRegion;

    @PostConstruct
    public void init() {
        defaultRegion = SystemConstant.ENV_DEV.equals(activeProfile) ? "cn" : "en";
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@within(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void doBefore() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String locale = request.getHeader(SystemConstant.REQUEST_HEADER_REGION);
        log.info("locale:{}", locale);
        String region = Optional.ofNullable(locale).map(e -> SystemConstant.LOCALE_HEADER_REGIONS.get(e)).orElse(defaultRegion);
        SupportDatasourceEnum datasourceEnum = SupportDatasourceEnum.getDatasource(region, DataSourceContextHolder.getDataSourceList());
        DataSourceContextHolder.setDatabaseHolder(datasourceEnum);
    }

    @After("pointCut()")
    public void doAfter() {
        DataSourceContextHolder.clear();
    }
}