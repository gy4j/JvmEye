package com.gy4j.jvm.eye.core.command;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public abstract class AbstractCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

    /**
     * 命令唯一ID
     */
    protected String commandId;

    /**
     * 会话ID
     */
    protected String sessionId;

    @Override
    public void execute(IClient client) {
        IResponse response = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("command:{},{}", this.getClass().getSimpleName(), JsonUtils.toJson(this));
            }
            // 执行模板方法，获取同步响应结果
            response = executeForResponse(client);
            if (logger.isDebugEnabled()) {
                logger.debug("response:{},{}", response.getClass().getSimpleName(), JsonUtils.toJson(response));
            }
            if (response != null) {
                client.write(this, response);
            } else {
                logger.warn("response is null, command: {},{}", this.getClass().getSimpleName(), JsonUtils.toJson(this));
            }
        } catch (Throwable ex) {
            logger.warn("命令执行异常:" + this.getClass().getSimpleName() + "，" + JsonUtils.toJson(this), ex);
            client.write(this, createExceptionResponse("命令执行异常:" + ex.getMessage()));
        }
    }

    /**
     * 通用方法，封装异常信息返回
     *
     * @param message
     * @return
     */
    protected IResponse createExceptionResponse(String message) {
        return BaseResponse.fail(message, getResponseClass());
    }

    /**
     * 命令执行，返回命令响应结果
     *
     * @param client
     * @return
     */
    public abstract IResponse executeForResponse(IClient client);


    @Override
    public String getCommandId() {
        return this.commandId;
    }

    @Override
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}