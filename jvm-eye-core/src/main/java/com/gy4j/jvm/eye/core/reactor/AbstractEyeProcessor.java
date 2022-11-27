package com.gy4j.jvm.eye.core.reactor;

import com.gy4j.jvm.eye.core.util.SocketChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static com.gy4j.jvm.eye.core.util.SocketChannelUtils.*;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public abstract class AbstractEyeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEyeProcessor.class);

    public static final int TIME_OUT = 15;

    private ByteBuffer byteBuffer = ByteBuffer.allocate(6);

    public void process(SelectionKey key) {
        try {
            // 如果读到客户端的数据
            SocketChannel clientSocket = (SocketChannel) key.channel();
            while (true) {
                // 小于0，服务器异常，断开连接
                logger.debug("读取之前 byteBuffer.position():" + byteBuffer.position());

                int readBytes = clientSocket.read(byteBuffer);
                if (readBytes < 0) {
                    logger.error("readBytes < 0");
                    handleReadException(key);
                    return;
                }
                // 等于0，说明没有可以读取的字节
                if (readBytes == 0) {
                    break;
                }

                logger.debug("读取之后 byteBuffer.position():" + byteBuffer.position());

                // 一次可能读取多个命令的bytes
                while (byteBuffer.position() >= HEADER_COUNT) {
                    int magicMark = -1;
                    for (int i = 0; i < (byteBuffer.position() - 1); i++) {
                        if (byteBuffer.get(i) == 0xb && byteBuffer.get(i + 1) == 0xa) {
                            magicMark = i;
                            break;
                        }
                    }

                    if (magicMark == -1) {
                        byte lastByte = byteBuffer.get(byteBuffer.position() - 1);
                        byteBuffer.clear();
                        byteBuffer.put(lastByte);
                        break;
                    }

                    if (magicMark > 0) {
                        byteBuffer.limit(byteBuffer.position());
                        byteBuffer.position(magicMark);
                        byteBuffer.compact();
                    }

                    // 前面的逻辑保证：第一位是0xb，第二位是0xa
                    // 保证能拿到4个字节的长度
                    if (byteBuffer.position() < HEADER_COUNT) {
                        break;
                    }

                    byteBuffer.flip();
                    // 检查第1位是0xb,第2位是0xa
                    if (!(byteBuffer.get() == MAGIC_BYTE_01 && byteBuffer.get() == MAGIC_BYTE_02)) {
                        throw new RuntimeException("What‘s wrong！！");
                    }
                    // 获取4字节的长度
                    int length = byteBuffer.getInt();

                    byte[] msgBytes = SocketChannelUtils.readBytes(clientSocket, length);
                    doRead(msgBytes, clientSocket);

                    byteBuffer.clear();
                }
            }
            key.interestOps(SelectionKey.OP_READ);
        } catch (Exception ex) {
            logger.error("读取错误：" + ex.getMessage(), ex);
            handleReadException(key);
        }
    }

    protected abstract void handleReadException(SelectionKey key);

    protected abstract void doRead(byte[] msgBytes, SocketChannel socketChannel);
}