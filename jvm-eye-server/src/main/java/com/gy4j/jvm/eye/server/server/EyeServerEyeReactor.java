package com.gy4j.jvm.eye.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/7-21:29
 * 版本       开发者     描述
 * 1.0.0     gy4j     NIO服务端的响应器
 */
public class EyeServerEyeReactor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(EyeServerEyeReactor.class);

    /**
     * 服务端
     */
    private EyeServer eyeServer;

    public EyeServerEyeReactor(EyeServer eyeServer) {
        this.eyeServer = eyeServer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 检查是否有响应数据需要处理
                int count = getSelector().selectNow();
                if (count == 0) {
                    continue;
                }
                // 获取响应事件列表
                Set<SelectionKey> selectionKeySet = getSelector().selectedKeys();
                Iterator<SelectionKey> iterable = selectionKeySet.iterator();
                SelectionKey key = null;
                // 遍历处理
                while (iterable.hasNext()) {
                    key = iterable.next();
                    // 可能异常cancel了，多一次检查减少冗余遍历
                    if (!key.isValid()) {
                        continue;
                    }
                    // 读事件
                    if (key.isReadable()) {
                        // 转给携带的processor处理，一个客户端一个processor，所有的processor共享一个线程池
                        EyeServerEyeProcessor processor = (EyeServerEyeProcessor) key.attachment();
                        if (processor != null) {
                            // 服务端多线程处理客户端响应
                            processor.processByThreadPool(key);
                        }
                    }
                    // 连接事件
                    else if (key.isAcceptable()) {
                        handleAccept();
                    }
                }
                // 可以大大减少SelectionKey信号重复的问题，详情见：EyeServerEyeProcessor的processByThreadPool备注
                TimeUnit.MILLISECONDS.sleep(100l);
                // 移除已处理的响应事件
                selectionKeySet.clear();
            } catch (Exception ex) {
                logger.error("run error:" + ex.getMessage(), ex);
            }
        }
    }

    /**
     * 处理客户端连接请求，可以优化为异步处理，但是连接数不大并且稳定，必要性不大
     *
     * @throws IOException
     */
    protected void handleAccept() throws IOException {
        logger.info("accept");
        // 如果accept被激活，说明有客户端连接
        SocketChannel newSocketChannel = eyeServer.getServerSocketChannel().accept();
        newSocketChannel.configureBlocking(false);
        newSocketChannel.socket().setKeepAlive(true);
        // 为客户端连接添注册读事件,并初始化携带的processor
        newSocketChannel.register(eyeServer.getSelector(), SelectionKey.OP_READ, new EyeServerEyeProcessor(eyeServer));
    }

    /**
     * 获取Selector
     *
     * @return
     */
    protected Selector getSelector() {
        return eyeServer.getSelector();
    }
}
