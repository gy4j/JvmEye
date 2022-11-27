package com.gy4j.jvm.eye.core.response;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public interface IResponse {
    /**
     * 设置客户端ID
     *
     * @param clientId
     */
    void setClientId(String clientId);

    /**
     * 获取客户端ID
     *
     * @return
     */
    String getClientId();

    /**
     * 设置命令ID
     *
     * @param commandId
     */
    void setCommandId(String commandId);

    /**
     * 获取命令ID
     *
     * @return
     */
    String getCommandId();
}
