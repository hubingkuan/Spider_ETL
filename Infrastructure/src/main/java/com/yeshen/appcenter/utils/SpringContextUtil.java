package com.yeshen.appcenter.utils;

import com.alibaba.ttl.TtlUnwrap;
import com.yeshen.appcenter.config.executor.MyExecutor;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.aop.framework.AopContext;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.Executor;

/**
 * Date 2022/05/31  18:03
 * author  by HuBingKuan
 */
public class SpringContextUtil {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);
    }

    public static void publishEvent(Object event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    /**
     * 获取aop代理对象
     *
     * @return 代理对象
     */
    public static <T> T getCurrentProxy() {
        return (T) AopContext.currentProxy();
    }

    public static String getTomcatExecutorStatus() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) ((TomcatWebServer) ((ServletWebServerApplicationContext) applicationContext).getWebServer())
                .getTomcat().getConnector().getProtocolHandler().getExecutor();
        return ThreadPoolStatusUtil.getThreadPoolStatus(executor);
    }

    public static String getMyThreadPoolStatus() {
        Executor threadPool = applicationContext.getBean(Executor.class);
        if (TtlUnwrap.isWrapper(threadPool)) {
            Executor executor = TtlUnwrap.unwrap(threadPool);
            MyExecutor myExecutor = (MyExecutor) executor;
            return ThreadPoolStatusUtil.getThreadPoolStatus(myExecutor);
        } else {
            throw new RuntimeException("IOC容器中的线程池非TTL包装的线程池");
        }
    }
}