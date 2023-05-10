package com.yeshen.appcenter.domain.enums;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

/**
 * Date 2022/09/07  23:55
 * author  by HuBingKuan
 * 枚举类型反序列化器 依靠EnumValue接口的value()实现
 */
public class EnumValueDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        final JSONLexer lexer = parser.lexer;
        final int token = lexer.token();
        Class cls = (Class) type;
        // 获取该枚举class的所有常量
        Object[] enumConstants = cls.getEnumConstants();
        // 如果实现了EnumValue接口的话使用value()比较
        if (EnumValue.class.isAssignableFrom(cls)) {
            if (token != JSONToken.LITERAL_INT) {
                throw new JSONException("parse enum " + cls.getName() + " error, EnumValue接口只支持int值传递");
            }
            // 拿到客户端传递的int值   lexer.stringVal()
            int value = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            for (Object enumConstant : enumConstants) {
                if (((EnumValue) enumConstant).value() == value) {
                    return (T) enumConstant;
                }
            }
            throw new JSONException("parse enum " + cls.getName() + " error, value : " + value + " ,value方法找不到对应值");
        } else {
            //没实现EnumValue接口的 默认的按名称(客户端传递是String)或者按ordinal(客户端传递的是int)
            if (token == JSONToken.LITERAL_INT) {
                int intValue = lexer.intValue();
                lexer.nextToken(JSONToken.COMMA);
                if (intValue < 0 || intValue > enumConstants.length) {
                    throw new JSONException("parse enum " + cls.getName() + " error, value : " + intValue);
                }
                return (T) enumConstants[intValue];
            } else if (token == JSONToken.LITERAL_STRING) {
                return (T) Enum.valueOf(cls, lexer.stringVal());
            } else {
                throw new JSONException("parse enum " + cls.getName() + " error, 不支持该枚举类型反序列化: " + JSONToken.name(token));
            }
        }
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}