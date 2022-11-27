package com.gy4j.jvm.eye.core.enhance;

import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class EnhanceAdvice {
    private final Class<?> clazz;
    private final String methodName;
    private final String methodDesc;
    private final Object target;
    private final Object[] params;
    private final Object returnObj;
    private final Throwable exception;
    private final boolean atBefore;
    private final boolean atException;
    private final boolean atAfter;

    public EnhanceAdvice(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] params, Object returnObj, Throwable exception, boolean atBefore, boolean atException, boolean atAfter) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.target = target;
        this.params = params;
        this.returnObj = returnObj;
        this.exception = exception;
        this.atBefore = atBefore;
        this.atException = atException;
        this.atAfter = atAfter;
    }

    public static EnhanceAdvice newForAfterReturning(Class<?> clazz, String methodName
            , String methodDesc, Object target, Object[] params, Object returnObject) {
        return new EnhanceAdvice(clazz, methodName, methodDesc, target, params, returnObject
                , null, false, false, true);
    }

    public static EnhanceAdvice newForAfterThrowing(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] params, Throwable throwable) {
        return new EnhanceAdvice(clazz, methodName, methodDesc, target, params, null
                , throwable, false, true, false);
    }

    public static EnhanceAdvice newForBefore(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] params) {
        return new EnhanceAdvice(clazz, methodName, methodDesc, target, params, null
                , null, true, false, false);
    }
}
