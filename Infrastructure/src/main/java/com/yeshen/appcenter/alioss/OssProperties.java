package com.yeshen.appcenter.alioss;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Date 2022/07/07  19:34
 * author  by HuBingKuan
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix ="aliyun.oss")
@ConditionalOnExpression("'${spring.application.name}'.equals('WebAppcenterETL')")
public class OssProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String bucketName;

    private String hostname;
}