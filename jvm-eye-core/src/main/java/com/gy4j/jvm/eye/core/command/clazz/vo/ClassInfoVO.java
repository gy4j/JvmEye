package com.gy4j.jvm.eye.core.command.clazz.vo;

import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import com.gy4j.jvm.eye.core.util.ClassUtils;
import lombok.Data;

/**
 * @author gy4j
 */
@Data
public class ClassInfoVO {
    /**
     * 类名（全路径）
     */
    private String className;
    /**
     * 类加载器
     */
    private String classloader;
    /**
     * 类加载器hashcode
     */
    private String classLoaderHash;
    /**
     * 类加载器列表
     */
    private String[] classloaders;
    /**
     * 类文件来源
     */
    private String codeSource;

    public ClassInfoVO() {

    }

    public ClassInfoVO(Class<?> clazz) {
        this.className = ClassUtils.getClassName(clazz);
        this.classLoaderHash = ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader());
        this.classloader = ClassUtils.getClassLoader(clazz);
        this.codeSource = ClassUtils.getCodeSource(clazz);
        this.classloaders = ClassUtils.getClassLoaders(clazz);
    }

}
