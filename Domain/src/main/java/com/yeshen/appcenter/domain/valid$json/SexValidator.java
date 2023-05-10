package com.yeshen.appcenter.domain.valid$json;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Date 2022/04/22  0:17
 * author by HuBingKuan
 * 自定义参数校验
 */
public class SexValidator implements ConstraintValidator<Sex,String> {
    private static final String MAN = "man";
    private static final String WOMAN = "woman";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 不为null才进行校验
        if (value != null) {
            if(!Objects.equals(value,MAN) && !Objects.equals(value,WOMAN)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}