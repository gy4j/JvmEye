package com.gy4j.jvm.eye.core.spy;

import com.gy4j.jvm.eye.core.listener.AbstractEnhanceAdviceListener;
import com.gy4j.jvm.eye.core.listener.AdviceListener;
import com.gy4j.jvm.eye.core.listener.AdviceListenerManager;
import com.gy4j.jvm.eye.core.listener.InvokeListener;
import com.gy4j.jvm.eye.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SpyImpl extends SpyAPI.AbstractSpy {
    private static final Logger logger = LoggerFactory.getLogger(SpyImpl.class);

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
        ClassLoader classLoader = clazz.getClassLoader();

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];
        List<AbstractEnhanceAdviceListener> listeners = AdviceListenerManager.queryAdviceListeners(classLoader, clazz.getName(),
                methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    adviceListener.before(clazz, methodName, methodDesc, target, args);
                } catch (Exception e) {
                    logger.warn("class: {}, methodInfo: {}", clazz.getName(), methodInfo, e);
                }
            }
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {
        ClassLoader classLoader = clazz.getClassLoader();

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];

        List<AbstractEnhanceAdviceListener> listeners = AdviceListenerManager.queryAdviceListeners(classLoader, clazz.getName(),
                methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    adviceListener.afterReturning(clazz, methodName, methodDesc, target, args, returnObject);
                } catch (Exception e) {
                    logger.warn("class: {}, methodInfo: {}", clazz.getName(), methodInfo, e);
                }
            }
        }
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {
        ClassLoader classLoader = clazz.getClassLoader();

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];

        List<AbstractEnhanceAdviceListener> listeners = AdviceListenerManager.queryAdviceListeners(classLoader, clazz.getName(),
                methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    adviceListener.afterThrowing(clazz, methodName, methodDesc, target, args, throwable);
                } catch (Exception e) {
                    logger.warn("class: {}, methodInfo: {}", clazz.getName(), methodInfo, e);
                }
            }
        }
    }

    @Override
    public void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {
        ClassLoader classLoader = clazz.getClassLoader();
        String[] info = StringUtils.splitInvokeInfo(invokeInfo);
        String owner = info[0];
        String methodName = info[1];
        String methodDesc = info[2];

        List<AbstractEnhanceAdviceListener> listeners = AdviceListenerManager.queryInvokeListeners(classLoader, clazz.getName(),
                owner, methodName, methodDesc);

        if (listeners != null) {
            for (AbstractEnhanceAdviceListener listener : listeners) {
                try {
                    InvokeListener invokeListener = (InvokeListener) listener;
                    invokeListener.invokeBeforeTracing(classLoader, owner, methodName, methodDesc, Integer.parseInt(info[3]));
                } catch (Exception e) {
                    logger.warn("class: {}, invokeInfo: {}", clazz.getName(), invokeInfo, e);
                }
            }
        }
    }

    @Override
    public void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {
        ClassLoader classLoader = clazz.getClassLoader();
        String[] info = StringUtils.splitInvokeInfo(invokeInfo);
        String owner = info[0];
        String methodName = info[1];
        String methodDesc = info[2];
        List<AbstractEnhanceAdviceListener> listeners = AdviceListenerManager.queryInvokeListeners(classLoader, clazz.getName(),
                owner, methodName, methodDesc);

        if (listeners != null) {
            for (AbstractEnhanceAdviceListener listener : listeners) {
                try {
                    InvokeListener invokeListener = (InvokeListener) listener;
                    invokeListener.invokeAfterTracing(classLoader, owner, methodName, methodDesc, Integer.parseInt(info[3]));
                } catch (Exception e) {
                    logger.warn("class: {}, invokeInfo: {}", clazz.getName(), invokeInfo, e);
                }
            }
        }
    }

    @Override
    public void atInvokeException(Class<?> clazz, String invokeInfo, Object target, Throwable throwable) {
        ClassLoader classLoader = clazz.getClassLoader();
        String[] info = StringUtils.splitInvokeInfo(invokeInfo);
        String owner = info[0];
        String methodName = info[1];
        String methodDesc = info[2];

        List<AbstractEnhanceAdviceListener> listeners = AdviceListenerManager.queryInvokeListeners(classLoader, clazz.getName(),
                owner, methodName, methodDesc);

        if (listeners != null) {
            for (AbstractEnhanceAdviceListener listener : listeners) {
                try {
                    InvokeListener invokeListener = (InvokeListener) listener;
                    invokeListener.invokeThrowTracing(classLoader, owner, methodName, methodDesc, Integer.parseInt(info[3]));
                } catch (Exception e) {
                    logger.warn("class: {}, invokeInfo: {}", clazz.getName(), invokeInfo, e);
                }
            }
        }
    }
}