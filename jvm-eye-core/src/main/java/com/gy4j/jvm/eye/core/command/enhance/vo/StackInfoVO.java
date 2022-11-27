package com.gy4j.jvm.eye.core.command.enhance.vo;

import com.gy4j.jvm.eye.core.command.enhance.model.node.ThreadNode;
import com.gy4j.jvm.eye.core.util.ThreadUtils;
import lombok.Data;

import java.util.Date;

/**
 * @author gy4j
 */
@Data
public class StackInfoVO {
    private Date ts;
    private double cost;
    private String threadName;
    private String threadId;
    private boolean daemon;
    private int priority;
    private String classLoader;
    private StackTraceElement[] stackTrace;

    public StackInfoVO() {

    }

    public StackInfoVO(double cost) {
        setTs(new Date());
        setCost(cost);

        Thread currentThread = Thread.currentThread();
        ThreadNode threadNode = ThreadUtils.getThreadNode(currentThread);
        setThreadName(threadNode.getThreadName());
        setThreadId(Long.toHexString(threadNode.getThreadId()));
        setDaemon(threadNode.isDaemon());
        setPriority(threadNode.getPriority());
        setClassLoader(threadNode.getClassLoader());

        //stack
        StackTraceElement[] stackTraceElementArray = currentThread.getStackTrace();
        int magicStackDepth = ThreadUtils.findTheSpyAPIDepth(stackTraceElementArray);
        StackTraceElement[] actualStackFrames = new StackTraceElement[stackTraceElementArray.length - magicStackDepth];
        System.arraycopy(stackTraceElementArray, magicStackDepth, actualStackFrames, 0, actualStackFrames.length);
        setStackTrace(actualStackFrames);
    }
}
