package com.yeshen.appcenter.domain.valid$json;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import lombok.AllArgsConstructor;

/**
 * Date 2022/06/17  16:59
 * author  by HuBingKuan
 * 自动配置指定序列化字段或忽略序列化字段
 * 当includes的size > 0时，属性必须在includes中才会被序列化，excludes优先于includes。
 */
@AllArgsConstructor
public class JsonPropertyPreFilter extends SimplePropertyPreFilter {
    public JsonPropertyPreFilter addExcludes(String... filterNames){
        for (int i = 0; i < filterNames.length; i++) {
            this.getExcludes().add(filterNames[i]);
        }
        return this;
    }

    public JsonPropertyPreFilter addIncludes(String... filterNames){
        for (int i = 0; i < filterNames.length; i++) {
            this.getIncludes().add(filterNames[i]);
        }
        return this;
    }
}