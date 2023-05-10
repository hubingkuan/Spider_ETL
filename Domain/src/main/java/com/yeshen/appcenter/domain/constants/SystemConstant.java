package com.yeshen.appcenter.domain.constants;

import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;

import java.util.*;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public final class SystemConstant {
    public static final int INIT_PAGESIZE = 24;
    public static final int HEADER_RELEASE=10;

    public static final String ENV_DEV = "dev";
    public static final String ENV_PRD = "prd";

    public static final String TRACE_ID="TraceId";


    public static final String PROJECT_NAME = "WebAppcenter_";
    public static final String MAIL_SUBJECT_RESULT_ERROR = PROJECT_NAME + "接口报错";
    public static final String REQUEST_HEADER_REGION = "locale";
    public static final String APPCENTER_BANNER_URL = "https://res09.bignox.com/appcenter/en/2022/07/webapp-center-banner.png";
    public static final String APPCENTER_JP_BANNER_URL = "https://res09.bignox.com/appcenter/jp/2022/08/lQDPJxain_-vRXLNAyDNB4CwjctYKwghtLUDC4LWzgCHAA_1920_800.jpg";
    public static final String NEED_RESPONSE_RESULT = "need_response_result";
    public static final String POST_NAME_PREFIX = "noxplay";
    public static final String APKNAME = "apkName";
    public static final String APKURL = "apkUrl";
    public static final String DOWNLOADCOUNT = "downloadCount";
    public static final String DOWNLOADURL = "downloadUrl";
    public static final String GAMEBANNER = "gameBanner";
    public static final String GAMEGRADE = "gameGrade";
    public static final String GAMEICON = "gameIcon";
    public static final String GAMEPECULIARITY = "gamePeculiarity";
    public static final String GAMEPHOTO = "gamePhoto";
    public static final String GAMEVERSION = "gameVersion";
    public static final String GAMEVIDEO = "gameVideo";
    public static final String APKPACKAGE = "apkPackage";
    public static final String UPDATETIME = "updateTime";
    public static final String GAMENAME = "gameName";
    public static final String SUFFIX_LOCATION = ".html";
    public static final String PREFIX_LABEL = "game-cate";
    public static final String URI_SEPARATOR = "/";




    public static final List<String> SPECIAL_HEADER_LINK_REGIONS = Collections.unmodifiableList(new ArrayList() {
        {
            add("ar");
        }
    });

    public static final Map<String,SupportDatasourceEnum> CRAWLER_FETCH_REGIONS = Collections.unmodifiableMap(new HashMap() {
        {
            put("US",SupportDatasourceEnum.PRD_EN);
            put("ES",SupportDatasourceEnum.PRD_ES);
            put("ID",SupportDatasourceEnum.PRD_ID);
            put("JP",SupportDatasourceEnum.PRD_JP);
            put("KR",SupportDatasourceEnum.PRD_KR);
            put("BR",SupportDatasourceEnum.PRD_PT);
            put("RU",SupportDatasourceEnum.PRD_RU);
            put("TH",SupportDatasourceEnum.PRD_TH);
            put("TW",SupportDatasourceEnum.PRD_TW);
            put("VN",SupportDatasourceEnum.PRD_VN);
            put("CN",SupportDatasourceEnum.PRD_CN);
        }
    });

    public static final Map<String, String> LOCALE_HEADER_REGIONS = Collections.unmodifiableMap(new HashMap() {
        {
            put("en", "en");
            put("zh-CN", "cn");
            put("es", "es");
            put("id", "id");
            put("ja", "jp");
            put("ko", "kr");
            put("pt", "pt");
            put("ru", "ru");
            put("th", "th");
            put("zh-TW", "tw");
            put("vi", "vn");
        }
    });


    public static final Map<String, SupportDatasourceEnum> GAME_BASE_DATABASE_MAPPING = Collections.unmodifiableMap(new HashMap() {
        {
            put("en", SupportDatasourceEnum.PRD_BLOG_EN);
            put("zh-TW", SupportDatasourceEnum.PRD_BLOG_TW);
            put("ja", SupportDatasourceEnum.PRD_BLOG_JP);
            put("ko", SupportDatasourceEnum.PRD_BLOG_KR);
            put("vi", SupportDatasourceEnum.PRD_BLOG_VN);
            put("th", SupportDatasourceEnum.PRD_BLOG_TH);
            put("zh-CN", SupportDatasourceEnum.PRD_BLOG_CN);
            put("ru", SupportDatasourceEnum.PRD_BLOG_RU);
            put("id", SupportDatasourceEnum.PRD_BLOG_ID);
            put("de", SupportDatasourceEnum.PRD_BLOG_DE);
            put("fr", SupportDatasourceEnum.PRD_BLOG_FR);
            put("ph", SupportDatasourceEnum.PRD_BLOG_PH);
            put("pt", SupportDatasourceEnum.PRD_BLOG_PT);
            put("es", SupportDatasourceEnum.PRD_BLOG_ES);
            put("my", SupportDatasourceEnum.PRD_BLOG_MY);
            put("it", SupportDatasourceEnum.PRD_BLOG_IT);
            put("tr", SupportDatasourceEnum.PRD_BLOG_TR);
            put("pl", SupportDatasourceEnum.PRD_BLOG_PL);
            put("in", SupportDatasourceEnum.PRD_BLOG_IN);
            //put("ar", SupportDatasourceEnum.PRD_BLOG_AR);
        }
    });


    public static final Map<String, String> SITEMAP_REGIONS = Collections.unmodifiableMap(new HashMap() {
        {
            put("en", "https://www.bignox.com/appcenter/");
            put("cn", "https://www.yeshen.com/appcenter/");
            put("es", "https://es.bignox.com/appcenter/");
            put("id", "https://id.bignox.com/appcenter/");
            put("jp", "https://jp.bignox.com/appcenter/");
            put("kr", "https://kr.bignox.com/appcenter/");
            put("pt", "https://pt.bignox.com/appcenter/");
            put("ru", "https://ru.bignox.com/appcenter/");
            put("th", "https://th.bignox.com/appcenter/");
            put("tw", "https://tw.bignox.com/appcenter/");
            put("vn", "https://vn.bignox.com/appcenter/");
            put("blog_kr", "https://kr.bignox.com/blog/");
        }
    });


    public static final Map<String, String> PRE_REGISTRATION = Collections.unmodifiableMap(new HashMap() {
        {
            put("en", "Pre-registration");
            put("cn", "新游预约");
            put("es", "Preinscripción");
            put("id", "Pra-Registrasi");
            put("jp", "事前登録");
            put("kr", "사전예약");
            put("pt", "Pré-registro");
            put("ru", "Предрегистрация");
            put("th", "ลงทะเบียนล่วงหน้า");
            put("tw", "事前預約");
            put("vn", "Đăng ký trước");
        }
    });

    public static final Map<String, String> POPULAR_GAME = Collections.unmodifiableMap(new HashMap() {
        {
            put("en", "Popular Games");
            put("cn", "热门游戏");
            put("es", "Juegos Populares");
            put("id", "Game populer");
            put("jp", "人気ゲーム");
            put("kr", "인기 게임");
            put("pt", "Jogos Populares");
            put("ru", "Популярные Игры");
            put("th", "เกมยอดนิยม");
            put("tw", "熱門遊戲");
            put("vn", "Game Hot");
        }
    });

    public static final Map<String, String> POPULAR_APPLICATION = Collections.unmodifiableMap(new HashMap() {
        {
            put("en", "Popular Applications");
            put("cn", "热门应用");
            put("es", "Aplicaciones Populares");
            put("id", "App populer");
            put("jp", "人気アプリ");
            put("kr", "인기 앱");
            put("pt", "Aplicativos Populares");
            put("ru", "Популярные Приложения");
            put("th", "แอพยอดนิยม");
            put("tw", "熱門應用程式");
            put("vn", "Ứng dụng Hot");
        }
    });
}