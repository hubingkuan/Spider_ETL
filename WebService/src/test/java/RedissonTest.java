import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Date 2022/08/04  19:50
 * author  by HuBingKuan
 * Redisson实现分布式锁
 */
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    public void tryLock(String lockName,long expireTime){
        RLock rLock = redissonClient.getLock(lockName);
        try {
            boolean isLocked = rLock.tryLock(expireTime, TimeUnit.MILLISECONDS);
            if (isLocked) {
                // TODO
            }
        } catch (Exception e) {
            rLock.unlock();
        }
    }


}