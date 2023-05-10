package com.yeshen.appcenter.config.executor;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 */
@Configuration
public class ExecutorConfig {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = Math.max(2 * CORE_POOL_SIZE, 10);
    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.AbortPolicy();

    @Bean(name = "asyncTaskExecutor")
    public ExecutorService asyncTaskExecutor() {
        // 设置有界队列能增加系统的稳定性和预警能力(无界队列可能撑爆内存，导致整个系统不可用)
        MyExecutor executor = new MyExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                60L, TimeUnit.MINUTES,
                new LinkedBlockingQueue(3000),
                new MyThreadFactory("WebAppCenter-task"),
                REJECTED_EXECUTION_HANDLER);
        return TtlExecutors.getTtlExecutorService(executor);
    }
}