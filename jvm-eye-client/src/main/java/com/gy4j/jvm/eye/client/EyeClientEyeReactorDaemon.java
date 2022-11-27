package com.gy4j.jvm.eye.client;

import com.gy4j.jvm.eye.core.command.client.ClientInfoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EyeClientEyeReactorDaemon extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(EyeClientEyeReactorDaemon.class);

    /**
     * 客户端
     */
    EyeClient eyeClient;

    /**
     * 是否初始化完成
     */
    private boolean established;

    public EyeClientEyeReactorDaemon(EyeClient eyeClient) {
        this.eyeClient = eyeClient;
        setName("EyeClientEyeReactorDaemon-" + eyeClient.getClientName());
        setDaemon(true);
        logger.info("EyeClientEyeReactorDaemon start：{}", getName());
    }

    @Override
    public void run() {
        while (true) {
            EyeClientEyeReactor reactor = null;
            try {
                // 创建Reactor
                reactor = new EyeClientEyeReactor(this, eyeClient);
                this.eyeClient.setReactor(reactor);
                logger.debug("try connect to server " + reactor.getServerAddress().toString());
                reactor.connectToServer();
                logger.debug("connect to server successful " + reactor.getServerAddress().toString());

                // 发送客户端信息
                new ClientInfoCommand().execute(eyeClient);
            } catch (Throwable ex) {
                logger.debug("connect to server unsuccessful address:{},ex:{}", reactor.getServerAddress().toString(), ex.getMessage());
                try {
                    // 连接失败休息10秒
                    TimeUnit.SECONDS.sleep(20l);
                } catch (InterruptedException e) {
                }
                continue;
            }
            while (established) {
                try {
                    synchronized (this) {
                        this.wait();
                        logger.warn("got a news about disconnection and reconnect to server"
                                + reactor.getServerAddress().toString());
                    }
                } catch (Throwable e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 设置守护线程的状态
     *
     * @param established
     */
    public void setEstablished(boolean established) {
        this.established = established;
    }

    public boolean isEstablished() {
        return established;
    }
}
