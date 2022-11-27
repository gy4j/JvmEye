package com.gy4j.jvm.eye.core.listener;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.enhance.StackCommand;
import com.gy4j.jvm.eye.core.command.enhance.response.StackAsyncResponse;
import com.gy4j.jvm.eye.core.command.enhance.vo.StackInfoVO;
import com.gy4j.jvm.eye.core.enhance.EnhanceAdvice;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class StackEnhanceListener extends AbstractTimesAdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(StackEnhanceListener.class);

    public StackEnhanceListener(IClient client, StackCommand command) {
        super(client, command);
    }

    @Override
    public void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
    }

    @Override
    public void afterReturning(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        final EnhanceAdvice advice = EnhanceAdvice.newForAfterReturning(clazz, methodName, methodDesc, target, args, returnObject);
        finish(advice);
    }

    @Override
    public void afterThrowing(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        final EnhanceAdvice advice = EnhanceAdvice.newForAfterThrowing(clazz, methodName, methodDesc, target, args, throwable);
        finish(advice);
    }

    @Override
    protected IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost) {
        StackAsyncResponse stackAsyncResponse = new StackAsyncResponse();
        StackInfoVO stackInfo = new StackInfoVO(cost);
        stackAsyncResponse.setStackInfo(stackInfo);
        return stackAsyncResponse;
    }
}
