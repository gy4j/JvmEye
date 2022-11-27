package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.constant.EyeConstants;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public final class ClassLoaderUtils {
    private ClassLoaderUtils() {

    }

    /**
     * 获取classLoader的hashCode
     *
     * @param classLoader
     * @return
     */
    public static String getClassLoaderHash(ClassLoader classLoader) {
        if (classLoader == null) {
            return EyeConstants.NONE;
        } else {
            return Integer.toHexString(classLoader.hashCode());
        }
    }

    /**
     * 根据类名模糊搜索类列表
     *
     * @param instrumentation
     * @param className
     * @return
     */
    public static List<Class<?>> findClasses(Instrumentation instrumentation
            , String className) {
        if (StringUtils.isBlank(className)) {
            return Collections.emptyList();
        }
        return findClasses(instrumentation, className, null);
    }

    /**
     * 根据类名模糊搜索类列表
     *
     * @param instrumentation
     * @param className
     * @param classLoaderHash
     * @return
     */
    public static List<Class<?>> findClasses(Instrumentation instrumentation
            , String className, String classLoaderHash) {
        Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        for (Class<?> clazz : loadedClasses) {
            // 检查累加器是否匹配
            if (classLoaderHash != null) {
                String hash = ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader());
                if (!classLoaderHash.equals(hash)) {
                    continue;
                }
            }
            // 检查类名是否匹配
            if (clazz.getName().indexOf(className) != -1) {
                clazzList.add(clazz);
            }
        }
        return clazzList;
    }

    /**
     * 根据类名和累加载器hash精确匹配
     *
     * @param instrumentation
     * @param className
     * @param classLoaderHash
     * @return
     */
    public static List<Class<?>> findClassesOnly(Instrumentation instrumentation
            , String className, String classLoaderHash) {
        Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        for (Class<?> clazz : loadedClasses) {
            String hash = ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader());
            if (clazz.getName().equals(className)
                    && (StringUtils.isBlank(classLoaderHash) || hash.equals(classLoaderHash))) {
                clazzList.add(clazz);
            }
        }
        return clazzList;
    }

    /**
     * 搜索classloader
     *
     * @param inst
     * @param hashCode
     * @return
     */
    public static ClassLoader getClassLoader(Instrumentation inst, String hashCode) {
        if (hashCode == null || hashCode.isEmpty()) {
            return null;
        }
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                if (Integer.toHexString(classLoader.hashCode()).equals(hashCode)) {
                    return classLoader;
                }
            }
        }
        return null;
    }

    /**
     * 搜索classloader
     *
     * @param inst
     * @param name
     * @return
     */
    public static ClassLoader getClassLoaderByName(Instrumentation inst, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                if (classLoader.getClass().getName().equals(name)) {
                    return classLoader;
                }
            }
        }
        return null;
    }
}
