package com.yeshen.appcenter;

import com.yeshen.appcenter.utils.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Date 2022/1/13/0013
 * author by HuBingKuan
 */
@MapperScan(basePackages="com.yeshen.appcenter.repository.mysql")
@EnableMongoRepositories(basePackages = "com.yeshen.appcenter.repository.mongo")
@SpringBootApplication()
public class WebAppcenterApplication {
    public static void main(String[] args) {
        SpringContextUtil.setApplicationContext(SpringApplication.run(WebAppcenterApplication.class, args));
    }
}