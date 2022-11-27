package com.gy4j.jvm.eye.server.websocket;

import com.gy4j.jvm.eye.core.command.enhance.ResetCommand;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.server.helper.CommandHelper;
import com.gy4j.jvm.eye.server.server.EyeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author gy4j
 * 功能：
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@ServerEndpoint("/ws")
@Component
public class WsServerEndpoint {
    static final Logger logger = LoggerFactory.getLogger(WsServerEndpoint.class);
    static ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    /**
     * 连接成功
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        String sessionId = session.getRequestParameterMap().get("sessionId").get(0);
        logger.info("连接成功：" + sessionId);
        sessions.put(sessionId, session);
    }

    /**
     * 连接关闭
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getRequestParameterMap().get("sessionId").get(0);
        logger.info("连接关闭：" + sessionId);
        sessions.remove(sessionId);
    }

    /**
     * 接收到消息
     *
     * @param text
     */
    @OnMessage
    public String onMsg(String text) throws IOException {
        return "servet 发送：" + text;
    }


    /**
     * 发送消息
     */
    public static void send(EyeServer eyeServer, IAsyncResponse response) {
        try {
            if (response != null && sessions.containsKey(response.getSessionId())) {
                synchronized (response.getSessionId().intern()) {
                    sessions.get(response.getSessionId()).getBasicRemote().sendText(JsonUtils.toJson(response));
                }
            } else {
                logger.warn("session 不存在：" + response.getSessionId());
                ResetCommand resetCommand = new ResetCommand();
                resetCommand.setSessionId(response.getSessionId());
                CommandHelper.dealCommand(eyeServer, response.getClientId(), resetCommand);
            }
        } catch (Exception ex) {
            logger.error("发送失败：" + ex.getMessage(), ex);
        }
    }
}
