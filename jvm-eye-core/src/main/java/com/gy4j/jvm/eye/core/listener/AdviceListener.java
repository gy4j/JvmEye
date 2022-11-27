package com.gy4j.jvm.eye.core.listener;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public interface AdviceListener {
    /**
     * 前置通知
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @param target     目标类实例
     *                   若目标为静态方法,则为null
     * @param args       参数列表
     * @throws Throwable 通知过程出错
     */
    void before(
            Class<?> clazz, String methodName, String methodDesc,
            Object target, Object[] args);

    /**
     * 返回通知
     *
     * @param clazz        类
     * @param methodName   方法名
     * @param methodDesc   方法描述
     * @param target       目标类实例
     *                     若目标为静态方法,则为null
     * @param args         参数列表
     * @param returnObject 返回结果
     *                     若为无返回值方法(void),则为null
     * @throws Throwable 通知过程出错
     */
    void afterReturning(
            Class<?> clazz, String methodName, String methodDesc,
            Object target, Object[] args,
            Object returnObject);

    /**
     * 异常通知
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @param target     目标类实例
     *                   若目标为静态方法,则为null
     * @param args       参数列表
     * @param throwable  目标异常
     * @throws Throwable 通知过程出错
     */
    void afterThrowing(
            Class<?> clazz, String methodName, String methodDesc,
            Object target, Object[] args,
            Throwable throwable);
}
