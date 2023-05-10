package com.yeshen.appcenter.domain.enums;

import lombok.Getter;

/**
 * Date 2022/10/18  9:54
 * author  by HuBingKuan
 * 测试枚举类使用接口和抽象方法
 */
@Getter
public enum TestEnum implements EnumValue{
    YELLOW(0){
        @Override
        public int color() {
            return 0;
        }
    }

    ;

    TestEnum(int code) {
        this.code = code;
    }

    int code;

    public  abstract int color();

    @Override
    public int value() {
        return code;
    }
}