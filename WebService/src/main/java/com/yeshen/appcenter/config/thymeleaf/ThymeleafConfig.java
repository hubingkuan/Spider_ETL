package com.yeshen.appcenter.config.thymeleaf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

/**
 * Date 2022/06/07  19:20
 * author  by HuBingKuan
 */
@Configuration
public class ThymeleafConfig {
    @Bean
    public SpringTemplateEngine springTemplateEngine(ApplicationContext applicationContext){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addTemplateResolver(xmlTemplateResolver(applicationContext));
        templateEngine.addTemplateResolver(htmlTemplateResolver(applicationContext));
        templateEngine.addTemplateResolver(textTemplateResolver(applicationContext));
        return templateEngine;
    }

    private ITemplateResolver xmlTemplateResolver(ApplicationContext applicationContext){
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setOrder(1);
        templateResolver.setPrefix("classpath:/xml/");
        templateResolver.setSuffix(".xml");
        templateResolver.setTemplateMode(TemplateMode.XML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);
        // 检查资源是否存在 不存在则用下一个模板解析器
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }

    private ITemplateResolver htmlTemplateResolver(ApplicationContext applicationContext){
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setOrder(2);
        resolver.setPrefix("classpath:/email/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        return resolver;
    }

    private ITemplateResolver textTemplateResolver(ApplicationContext applicationContext){
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setOrder(3);
        resolver.setPrefix("classpath:/email/");
        resolver.setSuffix(".txt");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        return resolver;
    }
}