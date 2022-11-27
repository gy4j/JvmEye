package com.gy4j.jvm.eye.core.command;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.response.IResponse;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public interface ICommand {
    /**
     * 获取命令的唯一ID
     *
     * @return
     */
    String getCommandId();

    /**
     * 设置命令的唯一ID
     *
     * @param commandId
     */
    void setCommandId(String commandId);

    /**
     * 会话ID
     *
     * @return
     */
    String getSessionId();

    /**
     * 会话ID
     *
     * @param sessionId
     */
    void setSessionId(String sessionId);

    /**
     * 命令执行
     *
     * @param client
     */
    void execute(IClient client);


    /**
     * 获取返回的ResponseClass
     *
     * @return
     */
    Class<? extends IResponse> getResponseClass();
}