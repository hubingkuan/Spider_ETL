

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 */
@Configuration
public class ExecutorConfig {
    public static final int CPUNUM = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = 2 * CPUNUM;
    private static final int MAX_POOL_SIZE = 200;
    private static final String THREAD_POOL_NAME_PREFIX = "async-task-thread-pool-";
    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    @Bean(name = "asyncTaskExecutor")
    public ExecutorService asyncTaskExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue(300), REJECTED_EXECUTION_HANDLER);
    }
}