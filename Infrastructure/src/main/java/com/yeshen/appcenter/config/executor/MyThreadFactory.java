package com.yeshen.appcenter.config.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Date 2022/08/25  16:21
 * author  by HuBingKuan
 */
@Slf4j
public class MyThreadFactory implements ThreadFactory {
    private final AtomicLong counter;
    private final String prefix;

    public MyThreadFactory(String prefix) {
        this.prefix = prefix;
        counter=new AtomicLong();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, prefix + "-" + this.counter.incrementAndGet());
        thread.setUncaughtExceptionHandler((Thread t,Throwable e)->{
            log.warn("{}线程出现异常,异常原因:{}",thread.getName(),e.getMessage());
            e.printStackTrace();
        });
        return thread;
    }
}