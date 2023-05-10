import com.yeshen.appcenter.WebAppcenterApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Date 2022/04/14  16:27
 * author by HuBingKuan
 */
@Slf4j
@SpringBootTest(classes = WebAppcenterApplication.class)
@RunWith(SpringRunner.class)
public class RedisTemplateTest {
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 存入 Redis 的 Token 键的前缀
     */
    private static final String IDEMPOTENT_TOKEN_PREFIX = "idempotent_token:";


    /**
     * 加锁  该锁可重入  但无法续时
     */
    public String tryLock(String lockName,long releaseTime){
        DefaultRedisScript<Long> lockScript = new DefaultRedisScript();
        lockScript.setResultType(Long.class);
        lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock.lua")));
        // 存入的线程信息的前缀
        String key = UUID.randomUUID().toString();
        // 执行脚本
        Long result = (Long) redisTemplate.execute(
                lockScript,
                Collections.singletonList(lockName),
                key + Thread.currentThread().getId(),
                releaseTime);
        if (result != null && result.intValue() == 1) {
            return key;
        } else {
            return null;
        }
    }

    /**
     * 解锁
     * 防止误删别人的锁  以key校验是否是自己的锁
     */
    public void unLock(String lockName,String key){
        DefaultRedisScript<Long> unlockScript= new DefaultRedisScript();
        unlockScript.setResultType(Long.class);
        unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));

        redisTemplate.execute(unlockScript,
                Collections.singletonList(lockName),
                key + Thread.currentThread().getId()
        );
        redisTemplate.execute(unlockScript, Collections.singletonList(lockName), key);
    }






    /**
     * 防重复Token令牌  幂等性测试
     * 客户端每次进入到页面的时候申请一次token
     * 创建 Token 存入 Redis，并返回该 Token
     * @param value 用于辅助验证的 value 值
     * @return 生成的 Token 串
     */
    public String generateToken(String value) {
        // 实例化生成 ID 工具对象
        String token = UUID.randomUUID().toString();
        // 设置存入 Redis 的 Key
        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        // 存储 Token 到 Redis，且设置过期时间为5分钟
        redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
        // 返回 Token
        return token;
    }

    /**
     * 验证 Token 正确性
     *
     * @param token token 字符串
     * @param value value 存储在Redis中的辅助验证信息
     * @return 验证结果
     */
    public boolean validToken(String token, String value) {
        // 设置 Lua 脚本，其中 KEYS[1] 是 key，KEYS[2] 是 value
        String script = "if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        // 根据 Key 前缀拼接 Key
        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        // 执行 Lua 脚本
        Long result = (Long) redisTemplate.execute(redisScript, Arrays.asList(key, value));
        // 根据返回结果判断是否成功成功匹配并删除 Redis 键值对，若果结果不为空和0，则验证通过
        if (result != null && result != 0L) {
            log.info("验证 token={},key={},value={} 成功", token, key, value);
            return true;
        }
        log.info("验证 token={},key={},value={} 失败", token, key, value);
        return false;
    }

    /**
     * 使用管道
     */
    @Test
    public void bitmapTest(){
        List<Object> results = redisTemplate.executePipelined(
                new RedisCallback<Object>() {
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
                        for(int i=0; i< 10; i++) {
                            stringRedisConn.rPop("myqueue");
                        }
                        return null;
                    }
                });
    }
}