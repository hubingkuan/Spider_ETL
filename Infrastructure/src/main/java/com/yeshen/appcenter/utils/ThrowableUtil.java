package com.yeshen.appcenter.utils;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * Date 2022/2/17/0017
 * author by HuBingKuan
 */
public class ThrowableUtil {
    public static String getStackTraceByPackage(Throwable e, String packageNamePrefix) {
        StringBuilder builder = new StringBuilder("\n").append(e);
        for (final StackTraceElement stackTraceElement : e.getStackTrace()) {
            if (!stackTraceElement.getClassName().startsWith(packageNamePrefix)) {
                continue;
            }
            builder.append("\n\tat ").append(stackTraceElement);
        }
        return builder.toString();
    }

    public static Throwable extractRealException(Throwable throwable) {
        //这里判断异常类型是否为CompletionException、ExecutionException，如果是则进行提取，否则直接返回。
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }
}