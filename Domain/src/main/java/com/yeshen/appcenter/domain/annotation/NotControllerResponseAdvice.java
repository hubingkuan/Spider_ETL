package com.yeshen.appcenter.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date 2022/08/17  16:19
 * author  by HuBingKuan
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public  @interface NotControllerResponseAdvice {
}