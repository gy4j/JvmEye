package com.gy4j.jvm.eye.core.command;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.listener.AbstractEnhanceAdviceListener;
import com.gy4j.jvm.eye.core.enhance.EnhancerAffect;
import com.gy4j.jvm.eye.core.enhance.EnhancerTransformer;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public abstract class EnhanceCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(EnhanceCommand.class);
    /**
     * 类名(精确匹配)
     */
    protected String className;
    /**
     * 方法名(可选，精确匹配)
     */
    protected String methodName;
    /**
     * 类加载器hashcode(可选，精确匹配)
     */
    protected String classLoaderHash;
    /**
     * 条件表达式(可选，格式：xxx)
     */
    protected String conditionExpress;
    /**
     * 执行次数限制
     */
    protected int numberOfLimit = 5;

    @Override
    public IResponse executeForResponse(IClient client) {
        try {
            Instrumentation instrumentation = client.getInstrumentation();
            List<Class<?>> clazzList = ClassLoaderUtils.findClassesOnly(instrumentation, className, classLoaderHash);

            EnhancerTransformer enhancerTransformer = new EnhancerTransformer(new HashSet<Class<?>>(clazzList)
                    , methodName, getAdviceListener(client), isTracing(), isSkipJDKTrace());

            EnhancerAffect enhancerAffect = enhancerTransformer.enhance(instrumentation);
            return getNormalResponse(enhancerAffect);
        } catch (Exception ex) {
            logger.warn("enhance err {}", ex.getMessage(), ex);
            return createExceptionResponse("enhance error:" + ex.getMessage());
        }
    }

    /**
     * 获取同步返回结果
     *
     * @param enhancerAffect
     * @return
     */
    public abstract IResponse getNormalResponse(EnhancerAffect enhancerAffect);

    /**
     * 抽象方法：获取切面侦听器
     *
     * @param client
     * @return
     */
    public abstract AbstractEnhanceAdviceListener getAdviceListener(IClient client);

    /**
     * 是否需要针对方法里面的方法调用增加切面
     *
     * @return
     */
    public abstract boolean isTracing();

    /**
     * 是否忽略JDK方法增加切面
     *
     * @return
     */
    public abstract boolean isSkipJDKTrace();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassLoaderHash() {
        return classLoaderHash;
    }

    public void setClassLoaderHash(String classLoaderHash) {
        this.classLoaderHash = classLoaderHash;
    }

    public String getConditionExpress() {
        return conditionExpress;
    }

    public void setConditionExpress(String conditionExpress) {
        this.conditionExpress = conditionExpress;
    }

    public int getNumberOfLimit() {
        return numberOfLimit;
    }

    public void setNumberOfLimit(int numberOfLimit) {
        this.numberOfLimit = numberOfLimit;
    }
}
