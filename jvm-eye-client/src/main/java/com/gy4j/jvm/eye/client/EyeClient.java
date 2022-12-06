package com.gy4j.jvm.eye.client;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.ICommand;
import com.gy4j.jvm.eye.core.command.client.ClientInfoCommand;
import com.gy4j.jvm.eye.core.enhance.EnhanceManager;
import com.gy4j.jvm.eye.core.response.IAsyncResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.core.util.SeqUtils;
import com.gy4j.jvm.eye.core.util.SerializeUtils;
import com.gy4j.jvm.eye.core.util.SocketChannelUtils;
import com.gy4j.jvm.eye.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.nio.channels.ClosedChannelException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EyeClient implements IClient {
    private static final Logger logger = LoggerFactory.getLogger(EyeClient.class);

    /**
     * 生成客户端的唯一ID
     */
    public String clientId = SeqUtils.getSeq();
    /**
     * EyeClientEyeReactor的创建守护线程
     */
    private EyeClientEyeReactorDaemon eyeClientEyeReactorDaemon;
    /**
     * 客户端的Reactor
     */
    private EyeClientEyeReactor reactor;
    /**
     * Instrumentation
     */
    private Instrumentation instrumentation;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 服务端IP
     */
    private String serverHost;
    /**
     * 服务端PORT
     */
    private int serverPort;

    /**
     * 心跳定时器，1分钟上报一次
     */
    private Timer heartBeatTimer;

    public EyeClient(Instrumentation instrumentation
            , String clientName, String serverHost, int serverPort) {
        init(instrumentation,clientName,serverHost,serverPort);
    }

    public EyeClient(Instrumentation instrumentation
            , String clientName, String serverHost
            , int serverPort, String clientId) {
        if (!StringUtils.isBlank(clientId)) {
            this.clientId = clientId;
        }
        init(instrumentation,clientName,serverHost,serverPort);
    }

    private void init(Instrumentation instrumentation, String clientName, String serverHost, int serverPort) {
        this.instrumentation = instrumentation;
        EnhanceManager.init(instrumentation);
        this.clientName = clientName;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        eyeClientEyeReactorDaemon = new EyeClientEyeReactorDaemon(this);
        eyeClientEyeReactorDaemon.start();

        heartBeatTimer = new Timer("Timer-EyeClientEyeReactor", true);
        final EyeClient eyeClient = this;
        heartBeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (eyeClientEyeReactorDaemon.isEstablished()) {
                    new ClientInfoCommand().execute(eyeClient);
                }
            }
        }, 0, 60000);
    }

    /**
     * 客户端响应结果返回
     *
     * @param command  命令
     * @param response 返回结果
     */
    @Override
    public void write(ICommand command, IResponse response) {
        response.setClientId(clientId);
        response.setCommandId(command.getCommandId());
        if (response instanceof IAsyncResponse) {
            ((IAsyncResponse) response).setSessionId(command.getSessionId());
        }
        if (reactor != null) {
            try {
                SocketChannelUtils.write(reactor.getSocketChannel(), SerializeUtils.encode(response));
            } catch (Exception ex) {
                if (ex instanceof ClosedChannelException) {
                    reactor.release();
                }
                logger.warn("写出异常:" + ex.getMessage(), ex);
            }
        } else {
            logger.warn("reactor is null：" + JsonUtils.toJson(response));
        }
    }

    /**
     * 获取Reactor
     *
     * @param reactor
     */
    public void setReactor(EyeClientEyeReactor reactor) {
        this.reactor = reactor;
    }

    /**
     * 获取服务端IP
     *
     * @return
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * 获取服务端PORT
     *
     * @return
     */
    public int getServerPort() {
        return serverPort;
    }


    /**
     * 获取Instrumentation
     *
     * @return
     */
    @Override
    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }

    /**
     * 获取客户端ID
     *
     * @return
     */
    @Override
    public String getClientId() {
        return clientId;
    }

    /**
     * 获取客户端名称
     *
     * @return
     */
    @Override
    public String getClientName() {
        return this.clientName;
    }
}
