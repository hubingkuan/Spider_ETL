package com.yeshen.appcenter.config.cache;

/**
 * Date 2022/05/09  13:46
 * author  by HuBingKuan
 * 基于注解的使用方法
 * @Cacheable 触发缓存入口（这里一般放在创建和获取的方法上,@Cacheable注解会先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存）
 * @CacheEvict 触发缓存的eviction（用于删除的方法上）
 * @CachePut 更新缓存且不影响方法执行（用于修改的方法上，该注解下的方法始终会被执行）
 * @Caching 将多个缓存组合在一个方法上（该注解可以允许一个方法同时设置多个注解）
 * @CacheConfig 在类级别设置一些缓存相关的共同配置（与其它缓存配合使用）
 *
 * caffeine提供了三种缓存填充策略:手动、同步加载、异步加载
 * 提供了三种回收策略:基于大小回收、基于时间回收、基于引用回收
 * 基于大小回收有两种方式:一种是基于缓存大小、一种是基于权重
 * 如果使用refreshAfterWrite配置,必须指定一个CacheLoader.
 * 不用该配置则无需这个bean,如上所述,该CacheLoader将关联被该缓存管理器管理的所有缓存，
 * 所以必须定义为CacheLoader<Object, Object>，自动配置将忽略所有泛型类型。
 * initialCapacity=[integer]: 初始的缓存空间大小
 * maximumSize=[long]: 缓存的最大条数
 * maximumWeight=[long]: 缓存的最大权重
 * expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期
 * expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期
 * refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
 * weakKeys: 打开key的弱引用
 * weakValues：打开value的弱引用
 * softValues：打开value的软引用
 * recordStats：开发统计功能
 * 注意：
 * expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准。
 * maximumSize和maximumWeight不可以同时使用
 * weakValues和softValues不可以同时使用
 *
 * // 根据缓存的计数进行驱逐
 * LoadingCache<String, Object> cache = Caffeine.newBuilder()
 *     .maximumSize(10000)
 *     .build(key -> function(key));
 *
 *
 * // 根据缓存的权重来进行驱逐（权重只是用于确定缓存大小，不会用于决定该缓存是否被驱逐）
 * LoadingCache<String, Object> cache1 = Caffeine.newBuilder()
 *     .maximumWeight(10000)
 *     .weigher(key -> function1(key))
 *     .build(key -> function(key));
 *
 * // 基于固定的到期策略进行退出
 * LoadingCache<String, Object> cache = Caffeine.newBuilder()
 *     .expireAfterAccess(5, TimeUnit.MINUTES)
 *     .build(key -> function(key));
 * LoadingCache<String, Object> cache1 = Caffeine.newBuilder()
 *     .expireAfterWrite(10, TimeUnit.MINUTES)
 *     .build(key -> function(key));
 *
 * // 基于不同的到期策略进行退出
 * LoadingCache<String, Object> cache2 = Caffeine.newBuilder()
 *     .expireAfter(new Expiry<String, Object>() {
 *         @Override  创建1秒后过期 必须使用纳秒
 *         public long expireAfterCreate(String key, Object value, long currentTime) {
 *             return TimeUnit.SECONDS.toNanos(1);
 *         }
 *
 *         @Override  更新后2秒过期 必须使用纳秒
 *         public long expireAfterUpdate(@Nonnull String s, @Nonnull Object o, long l, long l1) {
 *             return TimeUnit.SECONDS.toNanos(2);
 *         }
 *
 *         @Override  读3秒后过期  必须使用纳秒
 *         public long expireAfterRead(@Nonnull String s, @Nonnull Object o, long l, long l1) {
 *             return TimeUnit.SECONDS.toNanos(3);
 *         }
 *     }).build(key -> function(key));
 *
 * // 移除事件监听
 * Cache<String, Object> cache = Caffeine.newBuilder()
 *     .removalListener((String key, Object value, RemovalCause cause) ->
 *                      System.out.printf("Key %s was removed (%s)%n", key, cause))
 *     .build();
 *
 * // 写入外部缓存(比如redis)
 * LoadingCache<String, Object> cache2 = Caffeine.newBuilder()
 *     .writer(new CacheWriter<String, Object>() {
 *         @Override public void write(String key, Object value) {
 *             // 写入到外部存储
 *         }
 *         @Override public void delete(String key, Object value, RemovalCause cause) {
 *             // 删除外部存储
 *         }
 *     })
 *     .build(key -> function(key));
 * // 统计缓存命中率
 * Cache<String, Object> cache = Caffeine.newBuilder()
 *     .maximumSize(10_000)
 *     .recordStats()
 *     .build();
 *  通过使用Caffeine.recordStats(), 可以转化成一个统计的集合. 通过 Cache.stats() 返回一个CacheStats。
 *  CacheStats提供以下统计方法：
 *  hitRate(): 返回缓存命中率
 *  evictionCount(): 缓存回收数量
 *  averageLoadPenalty(): 加载新值的平均时间
 */
/*@Configuration
public class CacheConfiguration {
    *//**
     * 相当于在构建LoadingCache对象的时候 build()方法中指定过期之后的加载策略方法
     * 必须要指定这个Bean，refreshAfterWrite=60s属性才生效
     * @return
     *//*
    @Bean
    public CacheLoader<String, Object> cacheLoader() {
        CacheLoader<String, Object> cacheLoader = new CacheLoader<String, Object>() {
            @Override
            public Object load(String key) throws Exception {
                return null;
            }
            // 重写这个方法将oldValue值返回回去，进而刷新缓存
            @Override
            public Object reload(String key, Object oldValue) throws Exception {
                return oldValue;
            }
        };
        return cacheLoader;
    }
}*/