package com.yeshen.appcenter.utils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Date 2022/09/07  16:17
 * author  by HuBingKuan
 */
public class ThreadPoolStatusUtil {
    public static String getThreadPoolStatus(ThreadPoolExecutor executor) {
        BlockingQueue<Runnable> queue = executor.getQueue();
        return MessageFormat.format("线程池状态:{0},核心线程:{1},当前线程:{2},活跃线程:{3},同存最大线程:{4},最大线程:{5},队列总容量:{6},队列元素大小:{7},队列剩余容量:{8},阻塞队列类型:{9},任务总量:{10},已完成的任务数:{11},拒绝策略:{12},更新时间:{13}", !executor.isShutdown() ? "运行中" : "已关闭", executor.getCorePoolSize(), executor.getPoolSize(), executor.getActiveCount(), executor.getLargestPoolSize(), executor.getMaximumPoolSize(),
                queue.size() + queue.remainingCapacity(), queue.size(), queue.remainingCapacity(), queue.getClass().getSimpleName(), executor.getTaskCount(), executor.getCompletedTaskCount(), executor.getRejectedExecutionHandler().getClass().getSimpleName(), LocalDateTime.now().toString());
    }
}