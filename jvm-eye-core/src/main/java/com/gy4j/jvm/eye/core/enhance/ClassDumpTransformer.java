package com.gy4j.jvm.eye.core.enhance;

import com.gy4j.jvm.eye.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ClassDumpTransformer implements ClassFileTransformer {
    private static final Logger logger = LoggerFactory.getLogger(ClassDumpTransformer.class);

    /**
     * 需要增强或者转换的类集合
     */
    private Set<Class<?>> classesToEnhance;
    /**
     * 导出列表
     */
    private Map<Class<?>, File> dumpResult = new HashMap<Class<?>, File>();
    /**
     * 导出目录
     */
    private File directory = new File("./");

    public ClassDumpTransformer(Set<Class<?>> classSet) {
        this.classesToEnhance = classSet;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className
            , Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (classesToEnhance.contains(classBeingRedefined)) {
            File file = FileUtils.dumpClassIfNecessary(classBeingRedefined, classfileBuffer);
            if (file != null) {
                dumpResult.put(classBeingRedefined, file);
            }
        }
        return null;
    }

    /**
     * 获取导出列表
     *
     * @return
     */
    public Map<Class<?>, File> getDumpResult() {
        return dumpResult;
    }
}
