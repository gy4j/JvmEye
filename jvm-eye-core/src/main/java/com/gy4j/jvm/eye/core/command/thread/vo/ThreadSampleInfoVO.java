package com.gy4j.jvm.eye.core.command.thread.vo;

import com.gy4j.jvm.eye.core.util.StringUtils;
import lombok.Data;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.Objects;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class ThreadSampleInfoVO {
    protected long id;
    protected String name;
    protected Thread.State state;

    // Thread info
    protected String group;
    protected int priority;
    protected boolean interrupted;
    protected boolean daemon;

    // ThreadInfo info
    protected long blockedTime;
    protected long blockedCount;
    protected long waitedTime;
    protected long waitedCount;
    protected LockInfo lockInfo;
    protected String lockName;
    protected long lockOwnerId;
    protected String lockOwnerName;
    protected boolean nativeFlag;
    protected boolean suspended;
    protected String stackTrace;
    protected MonitorInfo[] lockedMonitors;
    protected LockInfo[] lockedSynchronizers;
    protected StackTraceElement[] stackTraces;

    // Sampler info
    protected double cpu;
    protected long deltaTime;
    protected long time;

    public ThreadSampleInfoVO() {

    }

    public void initThreadInfo(ThreadInfo threadInfo) {
        if (threadInfo == null) {
            return;
        }
        this.setId(threadInfo.getThreadId());
        this.setName(threadInfo.getThreadName());
        this.setState(threadInfo.getThreadState());

        this.setLockInfo(threadInfo.getLockInfo());
        this.setLockedMonitors(threadInfo.getLockedMonitors());
        this.setLockedSynchronizers(threadInfo.getLockedSynchronizers());
        this.setLockName(threadInfo.getLockName());
        this.setLockOwnerId(threadInfo.getLockOwnerId());
        this.setLockOwnerName(threadInfo.getLockOwnerName());
        this.setStackTraces(threadInfo.getStackTrace());
        this.setStackTrace(StringUtils.getStackTrace(getStackTraces()));
        this.setBlockedCount(threadInfo.getBlockedCount());
        this.setBlockedTime(threadInfo.getBlockedTime());
        this.setNativeFlag(threadInfo.isInNative());
        this.setSuspended(threadInfo.isSuspended());
        this.setWaitedCount(threadInfo.getWaitedCount());
        this.setWaitedTime(threadInfo.getWaitedTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadSampleInfoVO threadVO = (ThreadSampleInfoVO) o;

        if (id != threadVO.id) return false;
        return name != null ? name.equals(threadVO.name) : threadVO.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
