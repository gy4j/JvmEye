package com.gy4j.jvm.eye.core.command.enhance.model;

import com.gy4j.jvm.eye.core.command.enhance.model.node.MethodNode;
import com.gy4j.jvm.eye.core.command.enhance.model.node.ThreadNode;
import com.gy4j.jvm.eye.core.command.enhance.model.node.ThrowNode;

import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class TraceTree {

    private TraceNode current;

    private TraceNode root;
    private int nodeCount = 0;

    public TraceTree(ThreadNode root) {
        this.root = root;
        this.current = root;
    }

    public void begin(String className, String methodName, int lineNumber, boolean isInvoking) {
        TraceNode child = findChild(current, className, methodName, lineNumber);
        if (child == null) {
            child = new MethodNode(className, methodName, lineNumber, isInvoking);
            current.addChild(child);
        }
        child.begin();
        current = child;
        nodeCount += 1;
    }

    private TraceNode findChild(TraceNode node, String className, String methodName, int lineNumber) {
        List<TraceNode> childList = node.getChildren();
        if (childList != null) {
            //less memory than foreach/iterator
            for (int i = 0; i < childList.size(); i++) {
                TraceNode child = childList.get(i);
                if (matchNode(child, className, methodName, lineNumber)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * 节点匹配
     *
     * @param node
     * @param className
     * @param methodName
     * @param lineNumber
     * @return
     */
    private boolean matchNode(TraceNode node, String className, String methodName, int lineNumber) {
        if (node instanceof MethodNode) {
            MethodNode methodNode = (MethodNode) node;
            if (lineNumber != methodNode.getLineNumber()) return false;
            if (className != null ? !className.equals(methodNode.getClassName()) : methodNode.getClassName() != null)
                return false;
            return methodName != null ? methodName.equals(methodNode.getMethodName()) : methodNode.getMethodName() == null;
        }
        return false;
    }

    public void end() {
        current.end();
        if (current.parent() != null) {
            //TODO 为什么会到达这里？ 调用end次数比begin多？
            current = current.parent();
        }
    }

    public void end(boolean isThrow) {
        if (isThrow) {
            current.setMark("throws Exception");
            if (current instanceof MethodNode) {
                MethodNode methodNode = (MethodNode) current;
                methodNode.setThrow(true);
            }
        }
        this.end();
    }

    public void end(Throwable throwable, int lineNumber) {
        ThrowNode throwNode = new ThrowNode();
        throwNode.setException(throwable.getClass().getName());
        throwNode.setMessage(throwable.getMessage());
        throwNode.setLineNumber(lineNumber);
        current.addChild(throwNode);
        this.end(true);
    }

    public TraceNode getCurrent() {
        return current;
    }

    public TraceNode getRoot() {
        return root;
    }

    public int getNodeCount() {
        return nodeCount;
    }
}
