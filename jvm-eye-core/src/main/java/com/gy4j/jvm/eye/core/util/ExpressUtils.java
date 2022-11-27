package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.enhance.EnhanceAdvice;
import ognl.ClassResolver;
import ognl.DefaultMemberAccess;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ExpressUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExpressUtils.class);

    private static final MemberAccess MEMBER_ACCESS = new DefaultMemberAccess(true);
    private static final String KEY_COST = "cost";

    private ExpressUtils() {

    }

    /**
     * 检查是否满足表达式
     *
     * @param conditionExpress
     * @param enhanceAdvice
     * @param cost
     * @return
     */
    public static boolean check(String conditionExpress, EnhanceAdvice enhanceAdvice, double cost) {
        final Object ret = get(conditionExpress, enhanceAdvice, cost);
        return ret instanceof Boolean && (Boolean) ret;
    }

    /**
     * 获取表达式返回的值
     *
     * @param express
     * @param enhanceAdvice
     * @param cost
     * @return
     */
    public static Object get(String express, EnhanceAdvice enhanceAdvice, double cost) {
        try {
            OgnlContext context = createOgnlContext(enhanceAdvice.getClass().getClassLoader());
            context.put(KEY_COST, cost);
            return Ognl.getValue(express, context, enhanceAdvice);
        } catch (Exception e) {
            logger.warn("Error during evaluating the expression:", e);
            return null;
        }
    }

    /**
     * 获取表达式的值
     *
     * @param express
     * @param classLoader
     * @param object
     * @return
     */
    public static Object get(String express, ClassLoader classLoader, Object object) {
        try {
            OgnlContext context = createOgnlContext(classLoader);
            return Ognl.getValue(express, context, object);
        } catch (Exception e) {
            logger.warn("Error during evaluating the expression", e);
            String errorMsg = e.getMessage();
            if (e.getCause() != null) {
                errorMsg = errorMsg + " cause by " + e.getCause().getMessage();
            }
            return "Error during evaluating the expression:" + errorMsg;
        }
    }

    /**
     * 创建OgnlContext
     *
     * @param classLoader
     * @return
     */
    private static OgnlContext createOgnlContext(ClassLoader classLoader) {
        OgnlContext context = new OgnlContext();
        context.setMemberAccess(MEMBER_ACCESS);
        context.setClassResolver(new ClassLoaderClassResolver(classLoader));
        return context;
    }

    static class ClassLoaderClassResolver implements ClassResolver {
        private ClassLoader classLoader;

        private Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>(101);

        public ClassLoaderClassResolver(ClassLoader classLoader) {
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            this.classLoader = classLoader;
        }

        @Override
        public Class<?> classForName(String className, @SuppressWarnings("rawtypes") Map context)
                throws ClassNotFoundException {
            Class<?> result = null;

            if ((result = classes.get(className)) == null) {
                try {
                    result = classLoader.loadClass(className);
                } catch (ClassNotFoundException ex) {
                    if (className.indexOf('.') == -1) {
                        result = Class.forName("java.lang." + className);
                        classes.put("java.lang." + className, result);
                    }
                }
                if (result == null) {
                    return null;
                }
                classes.put(className, result);
            }
            return result;
        }
    }
}
