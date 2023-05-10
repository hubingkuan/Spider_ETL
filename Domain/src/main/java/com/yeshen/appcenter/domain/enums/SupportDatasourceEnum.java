package com.yeshen.appcenter.domain.enums;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date 2022/04/02  20:06
 * author by HuBingKuan
 */
@Getter
@AllArgsConstructor
public enum SupportDatasourceEnum {
    DEV_CN("jdbc:mysql://localhost:3306/wordpress_cn?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai", "ksnrtbs", "nx.no.1@#!"),
    DEV_TW("jdbc:mysql://localhost:3306/wordpress_tw?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useCursorFetch=true", "ksnrtbs", "nx.no.1@#!"),

    PRD_REPTILE("jdbc:mysql://localhost:3306/nox_launcher_us?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "nox_launcher", "aAPaYhQZciUkqdDY"),
    PRD_CN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com/wordpress_cn?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress", "dGhpcyBpcyBhIGV4YW1wbGU="),
    PRD_EN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_en?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_en", "rHUGUFSpsg8daJx9"),
    PRD_ES("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_es?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_es", "rHUGUFSpsg8daJx9aaa"),
    PRD_ID("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_id?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_id", "rHUGUFSpsg8daJx9aaaccc"),
    PRD_JP("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_jp?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_jp", "SFubjJHam6nXb4SK"),
    PRD_KR("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_kr?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_kr", "PBN8MJAXpWA6nfvu"),
    PRD_PT("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_pt?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_pt", "rHUGUFSpsg8daJx9aaa"),
    PRD_RU("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_ru?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_ru", "rHUGUFSpsg8daJx9aaa"),
    PRD_TH("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_th?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_th", "rHUGUFSpsg8daJx9aaabbb"),
    PRD_TW("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_tw?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_tw", "dO8B8AmKihY9wpWy"),
    PRD_VN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/appcenter_vn?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "appcenter_vn", "rHUGUFSpsg8daJx9aaa"),
    // todo  补充ar地区
    PRD_BLOG_CN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/www_cn_blog?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "miser02", "Www#BLOG2000"),
    PRD_BLOG_EN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_en?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_en", "Hhoi5wmVl4"),
    PRD_BLOG_ES("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_es?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_es", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_ID("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_id?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_id", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_JP("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_test_tw?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_jp", "B86NgRIMSrsvqqG7"),
    PRD_BLOG_KR("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_kr?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_kr", "SdekZMTwFlHPc5PY"),
    PRD_BLOG_PT("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_pt?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_pt", "Hhoi5wmVl4"),
    PRD_BLOG_RU("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_ru?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_ru", "Hhoi5wmVl4"),
    PRD_BLOG_TH("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_th?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_th", "M7WJ5jHtaRM9A53A"),
    PRD_BLOG_TW("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_tw?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_tw", "Hhoi5wmVl4"),
    PRD_BLOG_VN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/wordpress_vn?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "wordpress_vn", "CwnN9fm2GRQd0rlV"),
    PRD_BLOG_DE("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_de?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_de", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_FR("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_fr?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_fr", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_PH("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_ph?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_ph", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_MY("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_my?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_my", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_IT("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_it?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_it", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_TR("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_tr?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_tr", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_PL("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_pl?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_pl", "TUMTYpXlpNZUz2hU"),
    PRD_BLOG_IN("jdbc:mysql://rm-aaa.mysql.rds.aliyuncs.com:3306/blog_in?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai", "blog_in", "TUMTYpXlpNZUz2hU"),


    ;

    private String url;
    private String username;
    private String password;

    public static Set<SupportDatasourceEnum> getServiceDevSupportDatasource() {
        return Arrays.stream(SupportDatasourceEnum.values())
                .filter(e -> e.toString().startsWith(SystemConstant.ENV_DEV))
                .collect(Collectors.toSet());
    }

    public static Set<SupportDatasourceEnum> getETLDevSupportDatasource() {
        Set<SupportDatasourceEnum> result = Arrays.stream(SupportDatasourceEnum.values())
                .filter(e -> e.toString().startsWith(SystemConstant.ENV_DEV))
                .collect(Collectors.toSet());
        result.add(SupportDatasourceEnum.PRD_REPTILE);
        return result;
    }

    public static Set<SupportDatasourceEnum> getServicePrdSupportDatasource() {
        return Arrays.stream(SupportDatasourceEnum.values())
                .filter(e -> e.toString().startsWith(SystemConstant.ENV_PRD))
                .filter(e -> !PRD_REPTILE.equals(e))
                .filter(e -> !PRD_BLOG_TW.equals(e))
                .collect(Collectors.toSet());
    }

    public static Set<SupportDatasourceEnum> getETLPrdSupportDatasource() {
        return Arrays.stream(SupportDatasourceEnum.values())
                .filter(e -> e.toString().startsWith(SystemConstant.ENV_PRD))
                .collect(Collectors.toSet());
    }

    public static SupportDatasourceEnum getDatasource(String region, List<SupportDatasourceEnum> supportDatasourceEnums) {
        for (final SupportDatasourceEnum datasourceEnum : supportDatasourceEnums) {
            if (datasourceEnum.toString().contains(region)) {
                return datasourceEnum;
            }
        }
        return supportDatasourceEnums.get(0);
    }

    public static boolean checkSupportFullText(String datasourceName){
        return PRD_KR.name().equals(datasourceName)||PRD_JP.name().equals(datasourceName)||PRD_EN.name().equals(datasourceName)||PRD_TW.name().equals(datasourceName);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}