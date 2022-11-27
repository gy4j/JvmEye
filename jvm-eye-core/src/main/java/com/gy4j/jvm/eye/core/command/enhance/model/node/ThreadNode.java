package com.gy4j.jvm.eye.core.command.enhance.model.node;

import com.gy4j.jvm.eye.core.command.enhance.model.TraceNode;

import java.util.Date;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ThreadNode extends TraceNode {
    /**
     * 线程名称
     */
    private String threadName;
    /**
     * 线程ID
     */
    private long threadId;
    /**
     * 是否守护线程
     */
    private boolean daemon;
    /**
     * 优先级
     */
    private int priority;
    /**
     * 类加载器名称
     */
    private String classLoader;
    /**
     * 时间
     */
    private Date timestamp;

    public ThreadNode() {
        super("thread");
        timestamp = new Date();
    }

    public ThreadNode(String threadName, long threadId, boolean daemon, int priority, String classLoader) {
        super("thread");
        this.threadName = threadName;
        this.threadId = threadId;
        this.daemon = daemon;
        this.priority = priority;
        this.classLoader = classLoader;
        setTimestamp(new Date());
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(String classLoader) {
        this.classLoader = classLoader;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
