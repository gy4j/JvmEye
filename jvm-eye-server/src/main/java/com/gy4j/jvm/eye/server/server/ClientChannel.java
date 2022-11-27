package com.gy4j.jvm.eye.server.server;

import com.gy4j.jvm.eye.core.command.ICommand;
import com.gy4j.jvm.eye.core.command.client.vo.ClientInfoVO;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.core.util.SocketChannelUtils;
import com.gy4j.jvm.eye.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/8-10:34
 * 版本       开发者     描述
 * 1.0.0     gy4j     客户端连接
 */
@Data
public class ClientChannel {
    private static final Logger logger = LoggerFactory.getLogger(ClientChannel.class);

    /**
     * 服务端等待命令执行超时时间
     */
    public static int SECONDS_OF_WAIT_TIME = 15;

    /**
     * 命令的CountDownLatch集合，记录命令的超时等待锁
     * 命令返回或者超时的时候释放锁
     */
    public static final Map<String, CountDownLatch> countDownLatchMap = new HashMap<>();

    /**
     * 命令的返回结果临时存储，客户端返回的时候写入，锁释放的时候移除
     */
    public static final Map<String, IResponse> responseMap = new HashMap<>();

    /**
     * 客户端SocketChannel
     */
    private SocketChannel socketChannel;

    /**
     * 客户端信息ClientInfoVO
     */
    private ClientInfoVO clientInfoVO;

    /**
     * 服务端
     */
    private EyeServer eyeServer;

    /**
     * 发送命令
     *
     * @param command 命令
     * @return
     */
    public IResponse sendCommand(ICommand command) {
        String commandId = null;
        try {
            // 向客户端发送命令
            commandId = SocketChannelUtils.sendCommand(socketChannel, command);
            // 创建锁并同步等待
            CountDownLatch countDownLatch = new CountDownLatch(1);
            countDownLatchMap.put(commandId, countDownLatch);
            boolean timeout = countDownLatch.await(SECONDS_OF_WAIT_TIME, TimeUnit.SECONDS);
            // 超时返回异常
            if (!timeout) {
                return BaseResponse.fail("命令执行超时，未在" + SECONDS_OF_WAIT_TIME + "秒内接收到反馈.", command.getResponseClass());
            }
            // 正常获取返回结果
            IResponse response = responseMap.get(commandId);
            return response;
        } catch (Exception e) {
            logger.error("sendCommand error:" + e.getMessage(), e);
            return BaseResponse.fail("命令执行异常:" + e.getMessage(), command.getResponseClass());
        } finally {
            // 移除锁
            if (!StringUtils.isBlank(commandId)) {
                countDownLatchMap.remove(commandId);
            }
            // 移除临时返回结果
            responseMap.remove(commandId);
        }
    }

    /**
     * 处理返回结果
     *
     * @param response 返回结果
     */
    public static void handleResponse(IResponse response) {
        if (countDownLatchMap.containsKey(response.getCommandId())) {
            // 写入临时返回结果集合
            responseMap.put(response.getCommandId(), response);
            // 释放锁
            countDownLatchMap.get(response.getCommandId()).countDown();
        } else {
            // 超时返回的结果或者未知
            logger.warn("ignore response:{}", JsonUtils.toJson(response));
        }
    }
}
