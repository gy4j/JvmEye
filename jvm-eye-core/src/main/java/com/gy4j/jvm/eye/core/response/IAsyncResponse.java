package com.gy4j.jvm.eye.core.response;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26-16:36
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public interface IAsyncResponse extends IResponse {
    void setSessionId(String sessionId);

    String getSessionId();
}