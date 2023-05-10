package com.yeshen.appcenter.config.executor;

import com.yeshen.appcenter.utils.ThreadPoolStatusUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * Date 2022/08/25  16:09
 * author  by HuBingKuan
 */
@Slf4j
public class MyExecutor extends ThreadPoolExecutor {
    public MyExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void shutdown() {
        log.warn("线程池准备关闭shutdown,状态:{}",ThreadPoolStatusUtil.getThreadPoolStatus(this));
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        log.warn("线程池准备关闭shutdownNow,状态:{}",ThreadPoolStatusUtil.getThreadPoolStatus(this));
        return super.shutdownNow();
    }
}