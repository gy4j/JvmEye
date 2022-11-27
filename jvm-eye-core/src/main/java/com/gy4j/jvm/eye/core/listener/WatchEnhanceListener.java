package com.gy4j.jvm.eye.core.listener;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.enhance.WatchCommand;
import com.gy4j.jvm.eye.core.command.enhance.response.WatchAsyncResponse;
import com.gy4j.jvm.eye.core.command.enhance.vo.WatchInfoVO;
import com.gy4j.jvm.eye.core.enhance.EnhanceAdvice;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/11-7:45
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class WatchEnhanceListener extends AbstractTimesAdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(WatchEnhanceListener.class);

    private WatchCommand watchCommand;

    public WatchEnhanceListener(IClient client, WatchCommand watchCommand) {
        super(client, watchCommand);
        this.watchCommand = watchCommand;
    }

    @Override
    public void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
        if (watchCommand.isAtBefore()) {
            EnhanceAdvice advice = EnhanceAdvice.newForBefore(clazz, methodName, methodDesc, target, args);
            finish(advice);
        }
    }

    @Override
    public void afterReturning(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        EnhanceAdvice advice = EnhanceAdvice.newForAfterReturning(clazz, methodName, methodDesc, target, args, returnObject);
        if (watchCommand.isAtAfter()) {
            finish(advice);
        }
        atFinish(advice);
    }


    @Override
    public void afterThrowing(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        EnhanceAdvice advice = EnhanceAdvice.newForAfterThrowing(clazz, methodName, methodDesc, target, args, throwable);
        if (watchCommand.isAtException()) {
            finish(advice);
        }
        atFinish(advice);
    }

    /**
     * 检测atFinish的通知
     *
     * @param advice
     */
    private void atFinish(EnhanceAdvice advice) {
        if ((watchCommand.isAtFinish())
                || (!watchCommand.isAtBefore() && !watchCommand.isAtAfter() && !watchCommand.isAtException())) {
            finish(advice);
        }
    }

    @Override
    protected IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost) {
        WatchAsyncResponse asyncResponse = new WatchAsyncResponse();
        WatchInfoVO watchInfoVO = new WatchInfoVO(enhanceAdvice, watchCommand, cost);
        asyncResponse.setWatchInfo(watchInfoVO);
        return asyncResponse;
    }
}
