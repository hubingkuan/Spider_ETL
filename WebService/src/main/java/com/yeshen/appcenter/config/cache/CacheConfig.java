package com.yeshen.appcenter.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Data
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache-config")
public class CacheConfig {
 
    private Map<String, CacheSpec> specs;
 
    @Data
    public static class CacheSpec {
        private Integer expireTime;
        private Integer maxSize;
    }
 
    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        SimpleCacheManager manager = new SimpleCacheManager();
        if (specs != null) {
            List<CaffeineCache> caches = specs.entrySet().stream()
                            .map(entry -> buildCache(entry.getKey(), entry.getValue(), ticker))
                            .collect(Collectors.toList());
            manager.setCaches(caches);
        }
        return manager;
    }
 
    private CaffeineCache buildCache(String name, CacheConfig.CacheSpec cacheSpec, Ticker ticker) {
        final Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(cacheSpec.expireTime, TimeUnit.HOURS)
                .maximumSize(cacheSpec.maxSize)
                .initialCapacity(16)
                // 在写操作和偶尔的读操作中会进行周期性的过期事件的执行  过期事件的调度和出发将会在O(1)的时间复杂度内完成
                // 为了使过期更有效率 指定一共调度线程代替在缓存活动中对过期事件进行调度 Java9以上可以用Scheduler.systemScheduler()
                //.scheduler(Scheduler.systemScheduler())
                // 当测试基于时间的驱逐策略的时候  不需要坐在板凳上面等待现实时钟的转动,使用Ticker接口指定一个时间源可以避免苦苦等待时钟转动的麻烦
                .ticker(ticker);
                //  移除监听器异步加载缓存都会使用executor
                //.executor();
        return new CaffeineCache(name, caffeineBuilder.build());
    }
 
    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}