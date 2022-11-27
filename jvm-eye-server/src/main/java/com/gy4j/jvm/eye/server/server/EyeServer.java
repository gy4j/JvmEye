package com.gy4j.jvm.eye.server.server;

import com.gy4j.jvm.eye.core.command.ICommand;
import com.gy4j.jvm.eye.core.command.client.response.ClientInfoResponse;
import com.gy4j.jvm.eye.core.command.client.vo.ClientInfoVO;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.server.websocket.WsServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/10/6-11:52
 * 版本       开发者     描述
 * 1.0.0     gy4j     NIO服务端
 */
public class EyeServer {
    private static final Logger logger = LoggerFactory.getLogger(EyeServer.class);

    /**
     * 服务端侦听接口
     */
    private int port;

    /**
     * Selector
     */
    private Selector selector;
    /**
     * serverSocketChannel
     */
    private ServerSocketChannel serverSocketChannel;

    /**
     * 连接的客户端列表
     */
    private Map<String, ClientChannel> clientChannels = new HashMap<String, ClientChannel>();

    public EyeServer(int port) {
        this.port = port;
    }

    /**
     * 服务端启动
     */
    public void start() {
        try {
            // 打开ServerSocketChannel，用于监听客户端的连接，所有客户端连接的父管道
            serverSocketChannel = ServerSocketChannel.open();

            // 绑定监听接口，并设置为非阻塞
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            // 创建多路复用
            selector = Selector.open();

            // 将ServerSocketChannel注册到多路复用器Selector上，监听Accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 启动服务端的Reactor处理线程
            Thread reactorThread = new Thread(new EyeServerEyeReactor(this));
            reactorThread.setName("EyeServerEyeReactor");
            reactorThread.start();
        } catch (Exception ex) {
            logger.error("EyeServer start error:" + ex.getMessage(), ex);
        }
    }

    /**
     * 添加客户端连接，在客户端上报ClientInfoCommand的时候添加
     *
     * @param socketChannel 客户端连接
     * @param clientInfo    客户端信息
     */
    public synchronized void addClient(SocketChannel socketChannel, ClientInfoVO clientInfo) {
        ClientChannel clientChannel = null;
        if (clientChannels.containsKey(clientInfo.getClientId())) {
            clientChannel = clientChannels.get(clientInfo.getClientId());
        } else {
            clientChannel = new ClientChannel();
        }
        clientChannel.setClientInfoVO(clientInfo);
        clientChannel.setSocketChannel(socketChannel);
        clientChannel.setEyeServer(this);
        clientChannels.put(clientInfo.getClientId(), clientChannel);
    }

    /**
     * 发送命令
     *
     * @param clientId 客户端ID
     * @param command  命令
     * @return
     */
    public IResponse sendCommand(String clientId, ICommand command) {
        if (clientChannels.containsKey(clientId)) {
            return clientChannels.get(clientId).sendCommand(command);
        }
        return BaseResponse.fail("clientId 不存在：" + clientId, command.getResponseClass());
    }

    /**
     * 异步返回的消息处理，通过Websocket转发到前端
     *
     * @param response 异步返回消息
     */
    public void asyncResponse(IAsyncResponse response) {
        WsServerEndpoint.send(this, response);
    }

    /**
     * 移除客户连接，在客户端连接读取异常的时候自动移除
     *
     * @param channel 客户端连接
     */
    public synchronized ClientChannel removeClient(SocketChannel channel) {
        for (String key : clientChannels.keySet()) {
            ClientChannel clientChannel = clientChannels.get(key);
            if (clientChannel.getSocketChannel().equals(channel)) {
                clientChannels.remove(key);
                return clientChannel;
            }
        }
        return null;
    }

    /**
     * 获取连接的客户端列表
     *
     * @return
     */
    public Map<String, ClientChannel> getClients() {
        return clientChannels;
    }

    /**
     * 根据客户端ID获取客户端连接
     *
     * @param clientId 客户端ID
     * @return
     */
    public ClientChannel getClient(String clientId) {
        return clientChannels.get(clientId);
    }

    /**
     * 获取Selector
     *
     * @return
     */
    public Selector getSelector() {
        return this.selector;
    }

    /**
     * 获取ServerSocketChannel
     *
     * @return
     */
    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    /**
     * 返回结果处理
     *
     * @param response      返回结果
     * @param socketChannel 客户端连接
     */
    public void handleResponse(IResponse response, SocketChannel socketChannel) {
        // 如果是客户端信息，需要添加客户端信息进行登记
        if (response instanceof ClientInfoResponse) {
            addClient(socketChannel, ((ClientInfoResponse) response).getClientInfo());
            return;
        }
        // 异步返回结果处理，比如：watch、stack、trace相关命令的异步返回
        if (response instanceof IAsyncResponse) {
            asyncResponse((IAsyncResponse) response);
        } else {
            // 同步返回结果处理
            if (getClient(response.getClientId()) != null) {
                getClient(response.getClientId()).handleResponse(response);
            } else {
                logger.info("已断开连接的客户端返回果：" + JsonUtils.toJson(response));
            }
        }
    }
}
