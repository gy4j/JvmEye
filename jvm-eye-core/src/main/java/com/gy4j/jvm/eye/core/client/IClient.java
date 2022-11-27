package com.gy4j.jvm.eye.core.client;

import com.gy4j.jvm.eye.core.command.ICommand;
import com.gy4j.jvm.eye.core.response.IResponse;

import java.lang.instrument.Instrumentation;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public interface IClient {
    Instrumentation getInstrumentation();

    /**
     * 获取客户端的ID(应用实例)
     *
     * @return
     */
    String getClientId();

    /**
     * 获取客户端的名称(应用名称)
     *
     * @return
     */
    String getClientName();

    /**
     * 命令执行的响应结果写出
     *
     * @param command
     * @param response
     */
    void write(ICommand command, IResponse response);
}
