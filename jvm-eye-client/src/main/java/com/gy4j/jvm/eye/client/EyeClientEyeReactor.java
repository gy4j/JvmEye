package com.gy4j.jvm.eye.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EyeClientEyeReactor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(EyeClientEyeReactor.class);

    /**
     * 客户端
     */
    private EyeClient eyeClient;
    /**
     * Reactor守护线程
     */
    private EyeClientEyeReactorDaemon eyeClientEyeReactorDaemon;
    /**
     * reactor是否运行中
     */
    protected boolean done = true;
    /**
     * 服务端连接地址
     */
    private InetSocketAddress serverAddress;
    /**
     * Selector
     */
    final Selector selector;
    /**
     * SocketChannel
     */
    final SocketChannel socketChannel;
    /**
     * SelectionKey
     */
    SelectionKey selectKey;

    public EyeClientEyeReactor(EyeClientEyeReactorDaemon eyeClientEyeReactorDaemon, final EyeClient eyeClient) throws IOException {
        this.eyeClientEyeReactorDaemon = eyeClientEyeReactorDaemon;
        this.eyeClient = eyeClient;
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);
        serverAddress = new InetSocketAddress(eyeClient.getServerHost(), eyeClient.getServerPort());
    }

    public boolean connectToServer() throws IOException {
        // 连接服务端
        socketChannel.connect(serverAddress);
        // 启动Reactor线程
        Thread reactorThread = new Thread(this);
        reactorThread.setName("EyeClientEyeReactor");
        reactorThread.start();
        // 设置守护线程状态
        eyeClientEyeReactorDaemon.setEstablished(true);
        // 非阻塞设置好
        socketChannel.configureBlocking(false);
        socketChannel.socket().setKeepAlive(true);
        // 缓存区大小设置
        socketChannel.socket().setReceiveBufferSize(102400);
        socketChannel.socket().setSendBufferSize(102400);
        // selectKey注册读事件，并初始化携带的processor
        selectKey = socketChannel.register(selector, 0);
        selectKey.attach(new EyeClientEyeProcessor(eyeClient, this));
        selectKey.interestOps(SelectionKey.OP_READ);
        // selector.select唤醒
        selector.wakeup();
        return true;
    }

    @Override
    public void run() {
        try {
            while (done) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100l);
                } catch (InterruptedException e) {
                }
                int count = selector.selectNow();
                if (count == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterable = selectionKeySet.iterator();
                SelectionKey key = null;
                while (iterable.hasNext()) {
                    key = iterable.next();
                    if (key.isReadable() && key.isValid()) {
                        EyeClientEyeProcessor processor = (EyeClientEyeProcessor) key.attachment();
                        if (processor != null) {
                            processor.process(key);
                        }
                    }
                }
                selectionKeySet.clear();
            }
            logger.debug("thread exit");
            release();
        } catch (IOException ex) {
            logger.error("reactor run error:" + ex.getMessage(), ex);
            release();
        }
    }

    /**
     * 获取服务端地址
     *
     * @return
     */
    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    /**
     * 获取reactor守护线程对象
     *
     * @return
     */
    public EyeClientEyeReactorDaemon getEyeClientEyeReactorDaemon() {
        return eyeClientEyeReactorDaemon;
    }

    /**
     * 获取SocketChannel
     *
     * @return
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * 设置reactor运行状态
     *
     * @param done
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    public void release() {
        synchronized (getEyeClientEyeReactorDaemon()) {
            getEyeClientEyeReactorDaemon().setEstablished(false);
            getEyeClientEyeReactorDaemon().notifyAll();
            setDone(false);
        }
    }
}
