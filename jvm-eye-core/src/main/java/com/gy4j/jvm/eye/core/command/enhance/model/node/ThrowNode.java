package com.gy4j.jvm.eye.core.command.enhance.model.node;

import com.gy4j.jvm.eye.core.command.enhance.model.TraceNode;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ThrowNode extends TraceNode {
    private String exception;
    private String message;
    private int lineNumber;

    public ThrowNode() {
        super("throw");
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
