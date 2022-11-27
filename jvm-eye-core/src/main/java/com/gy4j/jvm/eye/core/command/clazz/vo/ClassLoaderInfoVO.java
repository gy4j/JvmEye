package com.gy4j.jvm.eye.core.command.clazz.vo;

import com.gy4j.jvm.eye.core.constant.EyeConstants;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import lombok.Data;

/**
 * @author gy4j
 */
@Data
public class ClassLoaderInfoVO {
    /**
     * 类加载器名称
     */
    private String name;
    /**
     * 类加载器类名
     */
    private String className;
    /**
     * 父的类加载器名称
     */
    private String parentName;
    /**
     * 类加载器的hashcode
     */
    private String classLoaderHash;
    /**
     * 加载类的总数
     */
    private int loadedClassCount;

    public ClassLoaderInfoVO() {

    }

    public ClassLoaderInfoVO(ClassLoader classLoader) {
        this.name = classLoader.toString();
        this.className = classLoader.getClass().getName();
        ClassLoader parent = classLoader.getParent();
        if (parent == null) {
            this.parentName = EyeConstants.NONE;
        } else {
            this.parentName = classLoader.getParent().toString();
        }
        this.classLoaderHash = ClassLoaderUtils.getClassLoaderHash(classLoader);
    }

    public void increase() {
        this.loadedClassCount++;
    }
}
