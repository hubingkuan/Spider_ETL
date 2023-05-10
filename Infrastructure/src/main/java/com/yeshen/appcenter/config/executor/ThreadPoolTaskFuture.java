package com.yeshen.appcenter.config.executor;

import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * Date 2022/1/20/0020
 * author by HuBingKuan
 */
@Component
public class ThreadPoolTaskFuture implements ApplicationContextAware {
    private static ExecutorService executor;

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(withMdc(supplier), executor);
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(withMdc(runnable), executor);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        executor = applicationContext.getBean(ExecutorService.class);
    }

    private static <U> Supplier<U> withMdc(Supplier<U> supplier) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            if (mdc != null) {
                MDC.setContextMap(mdc);
            }
            return supplier.get();
        };
    }

    private static Runnable withMdc(Runnable runnable) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            if (mdc != null) {
                MDC.setContextMap(mdc);
            }
            runnable.run();
        };
    }
}