package com.gy4j.jvm.eye.server.helper;

import com.gy4j.jvm.eye.core.command.ICommand;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.StringUtils;
import com.gy4j.jvm.eye.server.server.EyeServer;

import java.util.UUID;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/9-20:39
 * 版本       开发者     描述
 * 1.0.0     gy4j     命令处理转换工具
 */
public class CommandHelper<T> {

    /**
     * 命令处理，并获取返回结果
     *
     * @param eyeServer 服务端
     * @param clientId  客户端ID
     * @param command   命令
     * @return
     */
    public static <T> T dealCommand(EyeServer eyeServer, String clientId, ICommand command) {
        // 检查并补充commandId
        if (StringUtils.isBlank(command.getCommandId())) {
            String commandId = UUID.randomUUID().toString();
            command.setCommandId(commandId);
        }
        // 检查并补充sessionId
        if (StringUtils.isBlank(command.getSessionId())) {
            String sessionId = UUID.randomUUID().toString();
            command.setSessionId(sessionId);
        }
        // 发送命令并获取返回结果
        IResponse response = eyeServer.sendCommand(clientId, command);

        return (T) response;
    }
}
