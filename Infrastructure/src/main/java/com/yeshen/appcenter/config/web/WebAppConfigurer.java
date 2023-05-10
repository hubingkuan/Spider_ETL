package com.yeshen.appcenter.config.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yeshen.appcenter.config.interceptor.ResponseResultInterceptor;
import com.yeshen.appcenter.config.interceptor.TraceIdInterceptor;
import com.yeshen.appcenter.config.json.MyParserConfig;
import com.yeshen.appcenter.config.web.filter.HttpServletRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 */
@Slf4j
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
    @Autowired
    private ResourceProperties resourceProperties;

    @Autowired
    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;


    /**
     * 注意添加的第一个拦截器注入的bean会为null 因为并没有被Spring管理
     * 添加的第二个拦截器注入的bean不会为null 因为这个拦截器通过注入方法responseResultInterceptor()已经被Spring管理了
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceIdInterceptor()).addPathPatterns("/**").order(1);
        registry.addInterceptor(new ResponseResultInterceptor()).addPathPatterns("/**").order(2);

    }

    @Bean
    public Filter httpServletRequestFilter() {
        return new HttpServletRequestFilter();
    }

    @Bean
    public FilterRegistrationBean addHttpServletRequestFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        // 这样注册的Filter支持依赖注入
        registration.setFilter(httpServletRequestFilter());
        registration.addUrlPatterns("/*");
        registration.setName("HttpServletRequestFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

/*    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 如果禁用了静态资源映射的话
        if(!this.resourceProperties.isAddMappings()){
            log.warn("Default resource handling disabled");
        }
        // 对外暴露的访问路径 比如要访问resource/static下的静态资源 需要在请求路径上添加/static路径
        // 访问11.jpg的路径  http://localhost:7777/appcenter/static123/11.jpg
*//*        registry.addResourceHandler("/static123/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(24*3600*365);*//*
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }*/

    /**
     * 设置跨域
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //放行哪些原始域
                .allowedOrigins("*")
                //是否发送Cookie信息
                .allowCredentials(true)
                //放行哪些原始域(请求方式)
                .allowedMethods(RequestMethod.GET.name(), RequestMethod.POST.name(), RequestMethod.PUT.name(), RequestMethod.DELETE.name(), RequestMethod.OPTIONS.name())
                //放行哪些原始域(头部信息)
                .allowedHeaders("*")
                //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
                .exposedHeaders("access-control-allow-headers",
                        "access-control-allow-methods",
                        "access-control-allow-origin",
                        "access-control-max-age",
                        "X-Frame-Options",
                        "userAuth")
                .maxAge(3600);
    }


    /**
     * 配置返回http响应结果的转换器
     *
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 移除SpringBoot自带的Jackson转换器
        converters.removeIf(x -> x instanceof MappingJackson2HttpMessageConverter);
        converters.add(0, fastJsonHttpMessageConverter);
    }

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConver() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 这里的设置fastjson为转换器之后 jackson的注解就无效了: @JsonInclude(value= JsonInclude.Include.NON_EMPTY)
        fastJsonConfig.setSerializerFeatures(
                // 保留null的字段
                SerializerFeature.WriteMapNullValue,
                // list字段如果为null，输出为[]，而不是null
                SerializerFeature.WriteNullListAsEmpty,
                // 数值字段如果为null，输出为0，而不是null
                SerializerFeature.WriteNullNumberAsZero,
                // Boolean字段如果为null，输出为false，而不是null
                SerializerFeature.WriteNullBooleanAsFalse,
                // 字符类型字段如果为null，输出为""，而不是null
                SerializerFeature.WriteNullStringAsEmpty,
                // 实体类枚举序列化给前端使用toString(默认使用枚举的name方法)
                SerializerFeature.WriteEnumUsingToString
        );
        /*
         * 设置类级别的过滤器
         * SerializeConfig.getGlobalInstance().addFilter(A.class,filter);
         * 设置枚举类型序列化成JavaBean
         * SerializeConfig.globalInstance.configEnumAsJavaBean(OrderType.class);
         * 设置全局序列化器和反序列化器
         * ParserConfig.getGlobalInstance().putDeserializer();
         * SerializeConfig.getGlobalInstance().put(Character.class, new CharacterSerializer());
         * */
        fastJsonConfig.setParserConfig(new MyParserConfig());
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        // 设置全局日期格式化
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        // 支持Actuator的health、heapdump的响应头
        fastMediaTypes.add(MediaType.valueOf("application/vnd.spring-boot.actuator.v3+json"));
        fastMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        fastMediaTypes.add(MediaType.TEXT_HTML);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }


    /*    *//**
     * 自定义错误页面
     * @return
     *//*
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            ErrorPage page_400 = new ErrorPage(HttpStatus.NOT_ACCEPTABLE, "/4xx.html");
            factory.addErrorPages(page_400);
        };
    }*/

/*    @Bean  //这种方式注册servletContextListener 不注册ApplicationContextListener
    public ServletListenerRegistrationBean addListener(){
        ServletListenerRegistrationBean listenerBean = new ServletListenerRegistrationBean<>();
        return listenerBean;
    }*/
}