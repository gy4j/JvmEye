package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.constant.EyeConstants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ClassUtils {
    private ClassUtils() {

    }

    /**
     * 获取类的加载文件来源
     *
     * @param clazz
     * @return
     */
    public static String getCodeSource(final Class<?> clazz) {
        CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        if (null == cs || null == cs.getLocation() || null == cs.getLocation().getFile()) {
            return EyeConstants.UNKNOWN;
        }
        return cs.getLocation().getFile();
    }

    /**
     * 获取类的类加载器
     *
     * @param clazz
     * @return
     */
    public static String getClassLoader(Class<?> clazz) {
        if (clazz.getClassLoader() == null) {
            return EyeConstants.NONE;
        }
        return clazz.getClassLoader().toString();
    }

    /**
     * 获取类的类加载器列表
     *
     * @param clazz
     * @return
     */
    public static String[] getClassLoaders(Class<?> clazz) {
        if (clazz.getClassLoader() == null) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        ClassLoader classLoader = clazz.getClassLoader();
        while (classLoader != null) {
            result.add(0, classLoader.toString());
            classLoader = classLoader.getParent();
        }
        return result.toArray(new String[]{});
    }

    /**
     * 判定是否Lambda派生类
     *
     * @param clazz
     * @return
     */
    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }

    /**
     * 获取类名称列表
     *
     * @param classes
     * @return
     */
    public static String[] getClassNameList(Class[] classes) {
        List<String> list = new ArrayList<String>();
        for (Class anInterface : classes) {
            list.add(getClassName(anInterface));
        }
        return list.toArray(new String[0]);
    }

    /**
     * 翻译注解名
     *
     * @param annotations
     * @return
     */
    public static String[] getAnnotations(Annotation[] annotations) {
        List<String> list = new ArrayList<String>();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                list.add(getClassName(annotation.annotationType()));
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 翻译类名称
     *
     * @param clazz
     * @return
     */
    public static String getClassName(Class<?> clazz) {
        if (clazz.isArray()) {
            StringBuilder sb = new StringBuilder(clazz.getName());
            sb.delete(0, 2);
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("[]");
            return sb.toString();
        } else {
            return clazz.getName();
        }
    }


    /**
     * 翻译Modifier名
     *
     * @param mod
     * @param splitter
     * @return
     */
    public static String modifier(int mod, char splitter) {
        StringBuilder sb = new StringBuilder();
        if (Modifier.isAbstract(mod)) {
            sb.append("abstract").append(splitter);
        }
        if (Modifier.isFinal(mod)) {
            sb.append("final").append(splitter);
        }
        if (Modifier.isInterface(mod)) {
            sb.append("interface").append(splitter);
        }
        if (Modifier.isNative(mod)) {
            sb.append("native").append(splitter);
        }
        if (Modifier.isPrivate(mod)) {
            sb.append("private").append(splitter);
        }
        if (Modifier.isProtected(mod)) {
            sb.append("protected").append(splitter);
        }
        if (Modifier.isPublic(mod)) {
            sb.append("public").append(splitter);
        }
        if (Modifier.isStatic(mod)) {
            sb.append("static").append(splitter);
        }
        if (Modifier.isStrict(mod)) {
            sb.append("strict").append(splitter);
        }
        if (Modifier.isSynchronized(mod)) {
            sb.append("synchronized").append(splitter);
        }
        if (Modifier.isTransient(mod)) {
            sb.append("transient").append(splitter);
        }
        if (Modifier.isVolatile(mod)) {
            sb.append("volatile").append(splitter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
