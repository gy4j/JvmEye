package com.gy4j.jvm.eye.core.command.enhance.vo;

import com.gy4j.jvm.eye.core.command.enhance.model.TraceEntity;
import com.gy4j.jvm.eye.core.command.enhance.model.TraceNode;
import com.gy4j.jvm.eye.core.command.enhance.model.node.MethodNode;
import com.gy4j.jvm.eye.core.command.enhance.model.node.ThreadNode;
import com.gy4j.jvm.eye.core.command.enhance.model.node.ThrowNode;
import com.gy4j.jvm.eye.core.util.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class TraceInfoVO {
    private double cost;
    private double deep;
    private int nodeCount;

    private List<NodeInfoVO> nodeInfos = new ArrayList<>();
    private MethodNode maxCostNode;

    public TraceInfoVO() {

    }

    public TraceInfoVO(TraceEntity traceEntity, double cost) {
        setCost(cost);
        setDeep(traceEntity.getDeep());
        setNodeCount(traceEntity.getTree().getNodeCount());
        TraceNode traceNode = traceEntity.getTree().getRoot();

        visitRootNode(traceNode);
        maxCostNode = null;
    }

    private void visitRootNode(TraceNode root) {
        maxCostNode = null;
        findMaxCostNode(root);

        recursive(0, true, root, new Callback() {
            @Override
            public void callback(int deep, boolean isLast, TraceNode node) {
                NodeInfoVO nodeInfo = new NodeInfoVO();
                nodeInfo.setDeep(deep);
                nodeInfo.setLast(isLast);
                renderNode(nodeInfo, node);
                if (!StringUtils.isBlank(node.getMark())) {
                    nodeInfo.setMark(node.getMark());
                }
                nodeInfos.add(nodeInfo);
            }
        });
    }

    private void renderNode(NodeInfoVO nodeInfo, TraceNode node) {
        //render cost: [0.366865ms]
        if (node instanceof MethodNode) {
            MethodNode methodNode = (MethodNode) node;
            nodeInfo.setNodeType(NodeInfoVO.NODE_TYPE_METHOD);
            renderCost(nodeInfo, methodNode);
            if (node == maxCostNode) {
                nodeInfo.setMaxCostNode(true);
            } else {
                nodeInfo.setMaxCostNode(false);
            }
        }

        //render method name
        if (node instanceof MethodNode) {
            MethodNode methodNode = (MethodNode) node;
            nodeInfo.setNodeType(NodeInfoVO.NODE_TYPE_METHOD);
            //clazz.getName() + ":" + method.getName() + "()"
            nodeInfo.setClassName(methodNode.getClassName());
            nodeInfo.setMethodName(methodNode.getMethodName());
            nodeInfo.setLineNumber(methodNode.getLineNumber());
        } else if (node instanceof ThreadNode) {
            //render thread info
            ThreadNode threadNode = (ThreadNode) node;
            nodeInfo.setNodeType(NodeInfoVO.NODE_TYPE_THREAD);
            //ts=2020-04-29 10:34:00;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@18b4aac2
            nodeInfo.setTs(threadNode.getTimestamp());
            nodeInfo.setThreadName(threadNode.getThreadName());
            nodeInfo.setThreadId(threadNode.getThreadId());
            nodeInfo.setDaemon(threadNode.isDaemon());
            nodeInfo.setPriority(threadNode.getPriority());
            nodeInfo.setClassLoader(threadNode.getClassLoader());
        } else if (node instanceof ThrowNode) {
            ThrowNode throwNode = (ThrowNode) node;
            nodeInfo.setNodeType(NodeInfoVO.NODE_TYPE_EXCEPTION);
            nodeInfo.setLineNumber(throwNode.getLineNumber());
            nodeInfo.setException(throwNode.getException());
            nodeInfo.setMessage(throwNode.getMessage());
        } else {
            throw new UnsupportedOperationException("unknown trace node: " + node.getClass());
        }
    }

    private void renderCost(NodeInfoVO nodeInfo, MethodNode node) {
        if (node.getTimes() <= 1) {
            if (node.parent() instanceof ThreadNode) {
                nodeInfo.setCost(node.getCost());
            } else {
                MethodNode parentNode = (MethodNode) node.parent();
                nodeInfo.setCost(node.getCost());
                nodeInfo.setPercentage(node.getCost() * 100 / parentNode.getTotalCost());
            }
        } else {
            if (node.parent() instanceof ThreadNode) {
                nodeInfo.setMinCost(node.getMinCost());
                nodeInfo.setMaxCost(node.getMaxCost());
                nodeInfo.setTotalCost(node.getTotalCost());
                nodeInfo.setTimes(node.getTimes());
            } else {
                MethodNode parentNode = (MethodNode) node.parent();
                nodeInfo.setPercentage(node.getTotalCost() * 100 / parentNode.getTotalCost());
                nodeInfo.setMinCost(node.getMinCost());
                nodeInfo.setMaxCost(node.getMaxCost());
                nodeInfo.setTotalCost(node.getTotalCost());
                nodeInfo.setTimes(node.getTimes());
            }
        }
    }

    /**
     * 递归遍历
     */
    private void recursive(int deep, boolean isLast, TraceNode node, Callback callback) {
        callback.callback(deep, isLast, node);
        if (!isLeaf(node)) {
            List<TraceNode> children = node.getChildren();
            if (children == null) {
                return;
            }
            final int size = children.size();
            for (int index = 0; index < size; index++) {
                final boolean isLastFlag = index == size - 1;
                recursive(
                        deep + 1,
                        isLastFlag,
                        children.get(index),
                        callback
                );
            }
        }
    }

    /**
     * 查找耗时最大的节点，便于后续高亮展示
     *
     * @param node
     */
    private void findMaxCostNode(TraceNode node) {
        if (node instanceof MethodNode && !isRoot(node) && !isRoot(node.parent())) {
            MethodNode aNode = (MethodNode) node;
            if (maxCostNode == null || maxCostNode.getTotalCost() < aNode.getTotalCost()) {
                maxCostNode = aNode;
            }
        }
        if (!isLeaf(node)) {
            List<TraceNode> children = node.getChildren();
            if (children != null) {
                for (TraceNode n : children) {
                    findMaxCostNode(n);
                }
            }
        }
    }

    private boolean isLeaf(TraceNode node) {
        List<TraceNode> children = node.getChildren();
        return children == null || children.isEmpty();
    }

    private boolean isRoot(TraceNode node) {
        return node.parent() == null;
    }

    /**
     * 遍历回调接口
     */
    private interface Callback {

        void callback(int deep, boolean isLast, TraceNode node);

    }
}
