package com.gy4j.jvm.eye.core.listener;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.enhance.TraceCommand;
import com.gy4j.jvm.eye.core.command.enhance.model.TraceEntity;
import com.gy4j.jvm.eye.core.command.enhance.response.TraceAsyncResponse;
import com.gy4j.jvm.eye.core.command.enhance.vo.TraceInfoVO;
import com.gy4j.jvm.eye.core.enhance.EnhanceAdvice;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/9-16:46
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class TraceEnhanceListener extends AbstractTimesAdviceListener implements InvokeListener {
    private static final Logger logger = LoggerFactory.getLogger(TraceEnhanceListener.class);

    public TraceEnhanceListener(IClient client, TraceCommand command) {
        super(client, command);
    }

    @Override
    public void invokeBeforeTracing(ClassLoader classLoader, String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber) {
        getThreadLocalTraceEntity().getTree().begin(tracingClassName, tracingMethodName, tracingLineNumber, true);
    }

    @Override
    public void invokeThrowTracing(ClassLoader classLoader, String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber) {
        getThreadLocalTraceEntity().getTree().end(true);
    }

    @Override
    public void invokeAfterTracing(ClassLoader classLoader, String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber) {
        getThreadLocalTraceEntity().getTree().end();
    }

    @Override
    public void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        TraceEntity traceEntity = getThreadLocalTraceEntity();
        traceEntity.getTree().begin(clazz.getName(), methodName, -1, false);
        traceEntity.addDeep();

        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
    }

    @Override
    public void afterReturning(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        getThreadLocalTraceEntity().getTree().end();
        final EnhanceAdvice advice = EnhanceAdvice.newForAfterReturning(clazz, methodName, methodDesc, target, args, returnObject);
        finishing(advice);
    }

    @Override
    public void afterThrowing(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        int lineNumber = -1;
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace.length != 0) {
            lineNumber = stackTrace[0].getLineNumber();
        }

        getThreadLocalTraceEntity().getTree().end(throwable, lineNumber);
        final EnhanceAdvice advice = EnhanceAdvice.newForAfterThrowing(clazz, methodName, methodDesc, target, args, throwable);
        finishing(advice);
    }

    /**
     * 检查并通知
     */
    private void finishing(EnhanceAdvice enhanceAdvice) {
        // 本次调用的耗时
        TraceEntity traceEntity = getThreadLocalTraceEntity();
        if (traceEntity.getDeep() >= 1) { // #1817 防止deep为负数
            traceEntity.subDeep();
        }
        if (traceEntity.getDeep() == 0) {
            finish(enhanceAdvice);
        }
    }

    @Override
    protected IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost) {
        TraceAsyncResponse asyncResponse = new TraceAsyncResponse();
        TraceInfoVO traceInfo = new TraceInfoVO(getThreadLocalTraceEntity(), cost);
        asyncResponse.setTraceInfo(traceInfo);
        return asyncResponse;
    }
}
