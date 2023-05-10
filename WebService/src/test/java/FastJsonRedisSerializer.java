

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;


/**
 * Date 2022/2/8/0008
 * author by HuBingKuan
 * 本序列化器带有对象信息 @type
 * new FastJsonRedisSerializer(Object.class)无对象信息
 * GenericJackson2JsonRedisSerializer带有@class
 * GenericFastJsonRedisSerializer带有@type
 */

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Class<T> clazz;

    static {
        ParserConfig.getGlobalInstance().addAccept("com.yeshen.appcenter.domain");
        JSON.DEFAULT_GENERATE_FEATURE = SerializerFeature.config(
                JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.SkipTransientField, false);
    }

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (null == t) {
            return new byte[0];
        }

        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }
}