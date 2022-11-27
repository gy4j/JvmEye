package com.gy4j.jvm.eye.core.listener;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.EnhanceCommand;
import com.gy4j.jvm.eye.core.command.enhance.model.TraceEntity;
import com.gy4j.jvm.eye.core.enhance.EnhanceAdvice;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/14-14:39
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public abstract class AbstractTimesAdviceListener extends AbstractEnhanceAdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTimesAdviceListener.class);

    protected AtomicInteger times = new AtomicInteger(0);

    public AbstractTimesAdviceListener(IClient client, EnhanceCommand enhanceCommand) {
        super(client, enhanceCommand);
    }

    protected final ThreadLocal<TraceEntity> threadLocalTraceEntity = new ThreadLocal<TraceEntity>();

    /**
     * 获取threadLocal里面的traceEntity
     *
     * @return
     */
    protected TraceEntity getThreadLocalTraceEntity() {
        TraceEntity traceEntity = threadLocalTraceEntity.get();
        if (traceEntity == null) {
            traceEntity = new TraceEntity();
            threadLocalTraceEntity.set(traceEntity);
        }
        return traceEntity;
    }

    /**
     * 结束
     */
    protected void finish(EnhanceAdvice enhanceAdvice) {
        double cost = threadLocalWatch.costInMillis();
        try {
            boolean conditionResult = isConditionMatch(enhanceCommand.getConditionExpress(), enhanceAdvice, cost);
            if (conditionResult) {
                // 是否到达数量限制
                if (isLimitExceeded(enhanceCommand.getNumberOfLimit(), times.get())) {
                    close();
                } else {
                    IAsyncResponse response = getAsyncResponse(enhanceAdvice, cost);
                    client.write(enhanceCommand, response);
                }
                // 满足输出条件
                times.getAndIncrement();
            }
        } catch (Exception e) {
            logger.warn("finish failed.", e);
        } finally {
            threadLocalTraceEntity.remove();
        }
    }

    protected abstract IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost);
}
