package com.gy4j.jvm.eye.core.command.enhance.model;

import com.gy4j.jvm.eye.core.util.ThreadUtils;
import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class TraceEntity {
    protected TraceTree tree;
    protected int deep;

    public TraceEntity() {
        this.tree = createTraceTree();
        this.deep = 0;
    }

    private TraceTree createTraceTree() {
        return new TraceTree(ThreadUtils.getThreadNode(Thread.currentThread()));
    }

    public void addDeep() {
        deep++;
    }

    public void subDeep() {
        deep--;
    }
}
