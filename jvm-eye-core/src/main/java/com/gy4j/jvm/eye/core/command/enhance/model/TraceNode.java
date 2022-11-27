package com.gy4j.jvm.eye.core.command.enhance.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class TraceNode {
    /**
     * 父节点
     */
    protected TraceNode parent;

    /**
     * 子节点列表
     */
    protected List<TraceNode> children;

    /**
     * node type
     */
    private String type;
    /**
     * 备注信息
     */
    private String mark;

    public TraceNode(String type) {
        this.type = type;
    }

    public void addChild(TraceNode child) {
        if (children == null) {
            children = new ArrayList<TraceNode>();
        }
        this.children.add(child);
        child.setParent(this);
    }

    public List<TraceNode> getChildren() {
        return this.children;
    }

    public void setParent(TraceNode parent) {
        this.parent = parent;
    }

    public TraceNode parent() {
        return parent;
    }


    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public void begin() {
    }

    public void end() {
    }
}
