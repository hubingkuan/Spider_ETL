package com.yeshen.appcenter.domain.enums;

import lombok.Getter;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 */
@Getter
public enum ResultCode {
    SERVER_SUCCESS(0, "Success"),
    CRAWL_SUCCESS(200, "爬取完成"),
    SERVER_ERROR(500, "Server Error"),
    REQUEST_ERROR(400, "Request Error"),
    MethodArgumentNotValid(400,"Request parameter Not Valid"),
    ASSERT_ERROR(400,"Assert error"),
    GAME_NOT_EXIST(1000100, "Game Not Exist"),
    REMOTE_REQUEST_ERROR(1000101, "RestTemplate Request Error"),
    DOWNLOAD_ERROR(1000102, "图片下载失败，接收类型为html"),
    OSS_REQUEST_ERROR(1000103, "OSS拒绝请求"),
    OSS_NETWORK_ERROR(1000104, "OSS网络连接错误"),
    WP_METAVALUE_ERROR(1000105, "metaValue值异常"),
    IMAGE_NOT_EXIST(1000106, "该包名图片Icon找不到"),



    ;

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}