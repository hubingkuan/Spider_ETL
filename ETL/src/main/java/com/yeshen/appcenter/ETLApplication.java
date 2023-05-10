package com.yeshen.appcenter;

import com.yeshen.appcenter.utils.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Date 2022/07/07  19:13
 * author  by HuBingKuan
 */
@MapperScan(basePackages="com.yeshen.appcenter.repository.mysql")
@SpringBootApplication
public class ETLApplication {
    public static void main(String[] args) {
        SpringContextUtil.setApplicationContext(SpringApplication.run(ETLApplication.class,args));
    }
}