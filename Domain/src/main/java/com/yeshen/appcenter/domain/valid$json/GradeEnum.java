package com.yeshen.appcenter.domain.valid$json;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * Date 2022/09/05  23:21
 * author  by HuBingKuan
 */
@Getter
public enum GradeEnum {

    PRIMARY(1, "小学"),  SECONDORY(2, "中学"),  HIGH(3, "高中");

    GradeEnum(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    @EnumValue//标记数据库存的值是code
    private final int code;
    //。。。
    private final String descp;
}