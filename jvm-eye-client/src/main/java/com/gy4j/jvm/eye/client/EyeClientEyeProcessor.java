package com.gy4j.jvm.eye.client;

import com.gy4j.jvm.eye.core.command.ICommand;
import com.gy4j.jvm.eye.core.reactor.AbstractEyeProcessor;
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
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EyeClientEyeProcessor extends AbstractEyeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EyeClientEyeProcessor.class);

    /**
     * 处理线程池，注意拒绝策略
     */
    private final static ExecutorService executeService = new ThreadPoolExecutor(4, 8,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            new ThreadFactory() {
                private AtomicInteger seq = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("EyeClientEyeProcessor-" + seq.getAndIncrement());
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 客户端
     */
    EyeClient eyeClient;

    /**
     * 客户端Reactor
     */
    EyeClientEyeReactor reactor;

    public EyeClientEyeProcessor(EyeClient eyeClient
            , EyeClientEyeReactor reactor) {
        this.eyeClient = eyeClient;
        this.reactor = reactor;
    }

    /**
     * 读异常处理,关闭服务端连接,关闭reactor,释放reactorDaemon
     * 注意：windows会有断开连接的异常抛出，linux没有异常抛出，在读取SocketChannel的字节数时返回-1
     *
     * @param selectionKey SelectionKey
     */
    protected void handleReadException(SelectionKey selectionKey) {
        try {
            logger.info("disconnection event happened");
            selectionKey.channel().close();
            selectionKey.cancel();
            reactor.release();
        } catch (IOException e) {
            logger.error("handleReadException:" + e.getMessage());
        }
    }

    /**
     * 读取的消息字节数组转换Response处理
     *
     * @param msgBytes      返回结果的字节数组
     * @param socketChannel 客户端连接
     */
    @Override
    protected void doRead(byte[] msgBytes, SocketChannel socketChannel) {
        try {
            final ICommand command = SerializeUtils.decode(msgBytes);

            if (command != null) {
                Future<Boolean> future = executeService
                        .submit(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        logger.info("开始执行：" + JsonUtils.toJson(command));
                                        command.execute(eyeClient);
                                        return true;
                                    }
                                }
                        );
                try {
                    if (future.get(TIME_OUT, TimeUnit.SECONDS)) {
                        logger.info("执行成功：" + JsonUtils.toJson(command));
                    }
                } catch (InterruptedException e) {
                    logger.info("执行打断：" + JsonUtils.toJson(command));
                } catch (ExecutionException e) {
                    logger.info("执行错误：" + JsonUtils.toJson(command), e);
                } catch (TimeoutException e) {
                    logger.info("执行超时：" + JsonUtils.toJson(command));
                    future.cancel(true);
                }
            }
        } catch (Throwable ex) {
            logger.error("doRead error:" + ex.getMessage());
        }
    }
}
