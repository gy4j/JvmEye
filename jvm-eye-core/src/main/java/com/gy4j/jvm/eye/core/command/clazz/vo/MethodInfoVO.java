package com.gy4j.jvm.eye.core.command.clazz.vo;

import com.alibaba.deps.org.objectweb.asm.Type;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import com.gy4j.jvm.eye.core.util.ClassUtils;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author gy4j
 */
@Data
public class MethodInfoVO {
    /**
     * 类名（全路径）
     */
    private String className;
    /**
     * 类加载器
     */
    private String classloader;
    /**
     * 类加载器HashCode
     */
    private String classLoaderHash;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 参数类型
     */
    private String[] parameters;
    /**
     * 返回类型
     */
    private String returnType;
    private String[] exceptions;
    private String descriptor;
    private String modifier;
    private String[] annotations;


    public MethodInfoVO(Method method, Class<?> clazz) {
        this.className = clazz.getName();
        this.classloader = ClassUtils.getClassLoader(clazz);
        this.classLoaderHash = ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader());
        this.methodName = method.getName();
        this.modifier = ClassUtils.modifier(method.getModifiers(), ',');
        this.annotations = ClassUtils.getAnnotations(method.getDeclaredAnnotations());
        this.parameters = ClassUtils.getClassNameList(method.getParameterTypes());
        this.returnType = ClassUtils.getClassName(method.getReturnType());
        this.exceptions = ClassUtils.getClassNameList(method.getExceptionTypes());
        this.descriptor = Type.getMethodDescriptor(method);
    }
}
