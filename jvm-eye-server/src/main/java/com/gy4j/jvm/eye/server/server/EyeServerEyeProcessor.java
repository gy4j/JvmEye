package com.gy4j.jvm.eye.server.server;

import com.gy4j.jvm.eye.core.reactor.AbstractEyeProcessor;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.core.util.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/18-10:29
 * 版本       开发者     描述
 * 1.0.0     gy4j     NIO服务端的处理器
 */
public class EyeServerEyeProcessor extends AbstractEyeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EyeServerEyeProcessor.class);

    /**
     * 处理线程池，注意拒绝策略
     */
    private final static ExecutorService readService = new ThreadPoolExecutor(4, 8,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024 * 1024),
            new ThreadFactory() {
                private AtomicInteger seq = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("EyeServerEyeProcessor-" + seq.getAndIncrement());
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 服务端
     */
    private EyeServer eyeServer;

    public EyeServerEyeProcessor(EyeServer eyeServer) {
        this.eyeServer = eyeServer;
    }

    /**
     * 通过线程池处理
     * 实际一次SelectionKey的信号会产生很多次的任务submit,原因分析如下：
     * 读取到信号，提交异步任务，返回，会再次读取到同一个客户端的读信号（因为缓存在SocketChannel里面的信息还没被提取）,提交任务,返回......
     * 数据处理的结果就是：第一次异步任务读取SocketChannel里面所有的字节，后面的异步任务读取都为0，通过SelectionKeys的遍历后增加100ms的
     * 等待可以大大减少信号次数（非高性能要求项目，可以接受）
     *
     * @param key SelectionKey
     */
    public void processByThreadPool(final SelectionKey key) {
        if (key.channel() != null) {
            Future<Boolean> future = readService
                    .submit(new Callable<Boolean>() {
                                @Override
                                public Boolean call() {
                                    process(key);
                                    return true;
                                }
                            }
                    );
            try {
                future.get(TIME_OUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.info("读取打断：" + key);
            } catch (ExecutionException e) {
                logger.info("读取错误：" + key, e);
            } catch (TimeoutException e) {
                logger.info("读取超时：" + key);
                future.cancel(true);
            }
        }
    }

    /**
     * 读异常处理,关闭客户端连接、移除客户端
     * 注意：windows会有断开连接的异常抛出，linux没有异常抛出，在读取SocketChannel的字节数时返回-1
     *
     * @param selectionKey SelectionKey
     */
    protected void handleReadException(SelectionKey selectionKey) {
        try {
            if (selectionKey.channel() != null) {
                ClientChannel clientChannel = eyeServer.removeClient((SocketChannel) selectionKey.channel());
                logger.info("disconnection event happened:" + (clientChannel != null ? clientChannel.getClientInfoVO() : null));
                selectionKey.channel().close();
            }
            selectionKey.cancel();
        } catch (IOException e) {
            logger.error("handleReadException:" + e.getMessage(), e);
        }
    }

    /**
     * 读取的消息字节数组转换Response处理
     *
     * @param msgBytes      返回结果的字节数组
     * @param socketChannel 客户端连接
     */
    public void doRead(final byte[] msgBytes, final SocketChannel socketChannel) {
        final IResponse response = SerializeUtils.decode(msgBytes);
        Future<Boolean> future = readService
                .submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                synchronized (socketChannel) {
                                    try {
                                        logger.info("response开始处理:" + JsonUtils.toJson(response));
                                        eyeServer.handleResponse(response, socketChannel);
                                    } catch (Exception ex) {
                                        logger.error("response处理异常:" + ex.getMessage(), ex);
                                    }
                                }
                                return true;
                            }
                        }
                );
        try {
            future.get(TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info("Response处理打断：" + response);
        } catch (ExecutionException e) {
            logger.info("Response处理错误：" + response, e);
        } catch (TimeoutException e) {
            logger.info("Response处理超时：" + response);
            future.cancel(true);
        }

    }
}
