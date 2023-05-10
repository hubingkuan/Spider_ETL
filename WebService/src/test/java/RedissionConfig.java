import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Date 2022/08/04  19:48
 * author  by HuBingKuan
 */
@Configuration
public class RedissionConfig {
    @Value("spring.redis.host")
    private String redisHost;

    @Value("spring.redis.password")
    private String password;

    private int port=6379;

    @Bean
    public RedissonClient getRedisson(){
        Config config = new Config();
        config.useSingleServer().
                setAddress("redis://" + redisHost + ":" + port).
                setPassword(password);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

}