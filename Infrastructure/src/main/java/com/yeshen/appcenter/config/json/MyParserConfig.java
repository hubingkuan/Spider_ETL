package com.yeshen.appcenter.config.json;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.yeshen.appcenter.domain.enums.EnumValueDeserializer;

import java.lang.reflect.Type;

/**
 * Date 2022/09/07  23:58
 * author  by HuBingKuan
 * 全局枚举类型反序列化器(枚举类型并且实现了EnumValue接口的使用EnumValueDeserializer反序列化器)
 * 序列化格式参照SerializerFeature.WriteEnumUsingToString
 */
public class MyParserConfig extends ParserConfig {
    @Override
    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
        ObjectDeserializer derializer;
        if (clazz.isEnum()) {
            Class<?> deserClass = null;
            JSONType jsonType = clazz.getAnnotation(JSONType.class);
            if (jsonType != null) {
                deserClass = jsonType.deserializer();
                try {
                    derializer = (ObjectDeserializer) deserClass.newInstance();
                    this.putDeserializer(type,derializer);
                    return derializer;
                } catch (Throwable error) {
                    // skip
                }
            }
            //这里替换了原来的反序列化器。
            derializer = new EnumValueDeserializer();
            return derializer;
        }
        return super.getDeserializer(clazz, type);
    }
}