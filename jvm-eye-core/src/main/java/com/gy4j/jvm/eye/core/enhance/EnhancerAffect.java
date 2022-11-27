package com.gy4j.jvm.eye.core.enhance;

import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EnhancerAffect {
    /**
     * 开始时间
     */
    private final long start = currentTimeMillis();

    /**
     * 增强的类个数
     */
    private final AtomicInteger classCnt = new AtomicInteger();

    /**
     * 增强的方法个数
     */
    private final AtomicInteger methodCnt = new AtomicInteger();

    /**
     * 增强器
     */
    private ClassFileTransformer transformer;

    /**
     * 增强产生的异常
     */
    private Throwable throwable;

    /**
     * dumpClass的文件存放集合
     */
    private final Collection<File> classDumpFiles = new ArrayList<File>();

    /**
     * 增强的方法列表
     */
    private final List<String> methods = new ArrayList<String>();

    public EnhancerAffect() {
    }

    /**
     * 影响耗时(ms)
     *
     * @return 获取耗时(ms)
     */
    public long cost() {
        return currentTimeMillis() - start;
    }

    /**
     * 影响类统计
     *
     * @param cc 类影响计数
     * @return 当前影响类个数
     */
    public int classCnt(int cc) {
        return classCnt.addAndGet(cc);
    }

    /**
     * 影响方法统计
     *
     * @param mc 方法影响计数
     * @return 当前影响方法个数
     */
    public int methodCnt(int mc) {
        return methodCnt.addAndGet(mc);
    }

    /**
     * 记录影响的函数，并增加计数
     *
     * @return
     */
    public int addMethodAndCount(ClassLoader classLoader, String clazz, String method, String methodDesc) {
        this.methods.add(ClassLoaderUtils.getClassLoaderHash(classLoader) + "|" + clazz.replace('/', '.') + "#" + method + "|" + methodDesc);
        return methodCnt.addAndGet(1);
    }

    /**
     * 获取影响类个数
     *
     * @return 影响类个数
     */
    public int classCnt() {
        return classCnt.get();
    }

    /**
     * 获取影响方法个数
     *
     * @return 影响方法个数
     */
    public int methodCnt() {
        return methodCnt.get();
    }

    /**
     * 添加dumpClass文件
     *
     * @param file
     */
    public void addClassDumpFile(File file) {
        classDumpFiles.add(file);
    }

    public void setTransformer(ClassFileTransformer transformer) {
        this.transformer = transformer;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Collection<File> getClassDumpFiles() {
        return classDumpFiles;
    }

    public List<String> getMethods() {
        return methods;
    }
}
