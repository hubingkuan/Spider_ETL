package redisLock;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class RedisDistributedLock {
    private RedisTemplate<String, String> redisTemplate;
    private DelayQueue<DelayTask> delayQueue;
    private ConcurrentHashMap<String, DelayTask> delayTaskMap;

    private static final String LOCK_PREFIX = "lock-";
    private static final Long DEFAULT_EXPIRE_TIME = 30000L;
    private static final Long RENEW_TIME = 5000L;

    public RedisDistributedLock (RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        delayQueue = new DelayQueue<>();
        delayTaskMap = new ConcurrentHashMap<>();
        startTask();
    }

    /**
     * 获取锁
     *
     * @param key      锁的key
     * @param value    锁的值
     * @param lockTime 锁的过期时间
     * @return true表示获取锁成功，false表示获取锁失败
     */
    public boolean lock (String key, String value, Long lockTime) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(getLockKey(key), value, DEFAULT_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        if (Boolean.FALSE.equals(result)) {
            return false;
        }
        DelayTask delayTask = new DelayTask(getDelayKey(key), lockTime - RENEW_TIME);
        delayTaskMap.put(getDelayKey(key), delayTask);
        delayQueue.offer(delayTask);
        return true;
    }

    /**
     * 释放锁
     *
     * @param key   锁的key
     * @param value 锁的值
     * @return true表示释放锁成功，false表示释放锁失败
     */
    public boolean unlock (String key, String value) {
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis (RedisConnection connection) throws DataAccessException {
                Boolean result = null;
                String lockKey = getLockKey(key);
                while (result == null) {
                    connection.watch(lockKey.getBytes());
                    if (value.getBytes().equals(connection.get(lockKey.getBytes()))) {
                        connection.multi();
                        connection.del(lockKey.getBytes());
                        connection.del(getDelayKey(key).getBytes());
                        List<Object> execList = connection.exec();
                        if (!execList.isEmpty()) {
                            result = (Boolean) execList.get(0);
                        }
                    } else {
                        connection.unwatch();
                        break;
                    }
                }
                return result;
            }
        });
        DelayTask delayTask = delayTaskMap.remove(getDelayKey(key));
        if (delayTask != null) {
            delayQueue.remove(delayTask);
        }
        return true;
    }

    private void startTask () {
        Runnable task = () -> {
            try {
                while (true) {
                    DelayTask delayTask = delayQueue.take();
                    String delayKey = delayTask.getKey();
                    DelayTask newDelayTask = new DelayTask(delayKey, delayTask.getDelayTime() - RENEW_TIME);
                    delayQueue.offer(newDelayTask);
                    redisTemplate.expire(getLockKey(delayKey), DEFAULT_EXPIRE_TIME, TimeUnit.MILLISECONDS);
                    delayTaskMap.put(delayKey, newDelayTask);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        new Thread(task).start();
    }

    private String getLockKey (String key) {
        return LOCK_PREFIX + key;
    }

    private String getDelayKey (String key) {
        return "delay-" + key;
    }

    private static class DelayTask implements Delayed {
        private String key;
        private long executeTime; // 执行时间
        private long delayTime; // 延迟时间

        DelayTask (String key, long delayTime) {
            this.key = key;
            this.delayTime = delayTime;
            this.executeTime = System.currentTimeMillis() + delayTime;
        }

        @Override
        public long getDelay (TimeUnit unit) {
            return unit.convert(this.executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo (Delayed o) {
            if (this.executeTime < ((DelayTask) o).executeTime) {
                return -1;
            }
            if (this.executeTime > ((DelayTask) o).executeTime) {
                return 1;
            }
            return 0;
        }

        public String getKey () {
            return key;
        }

        public long getDelayTime () {
            return delayTime;
        }
    }
}