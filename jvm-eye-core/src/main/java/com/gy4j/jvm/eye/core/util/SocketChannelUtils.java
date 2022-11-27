package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.command.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SocketChannelUtils {
    public static final byte MAGIC_COUNT = 2;
    public static final byte LENGTH_COUNT = 4;
    public static final byte HEADER_COUNT = MAGIC_COUNT + LENGTH_COUNT;
    public static final byte MAGIC_BYTE_01 = 0xb;
    public static final byte MAGIC_BYTE_02 = 0xa;

    public static int READ_WRITE_BUFFER_BYTES = 1024;

    private static final Logger logger = LoggerFactory.getLogger(SocketChannelUtils.class);

    private SocketChannelUtils() {

    }

    /**
     * 写出信息
     *
     * @param socketChannel
     * @param bytes
     */
    public static void write(SocketChannel socketChannel, byte[] bytes) throws IOException {
        synchronized (socketChannel) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(6);
            byteBuffer.put(MAGIC_BYTE_01);
            byteBuffer.put(MAGIC_BYTE_02);
            byteBuffer.putInt(bytes.length);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            ByteBuffer msgByteBuffer = ByteBuffer.allocate(1024);
            for (int start = 0; start < bytes.length; ) {
                if ((bytes.length - start) < READ_WRITE_BUFFER_BYTES) {
                    msgByteBuffer.put(bytes, start, (bytes.length - start));
                } else {
                    msgByteBuffer.put(bytes, start, READ_WRITE_BUFFER_BYTES);
                }
                msgByteBuffer.flip();
                start = start + READ_WRITE_BUFFER_BYTES;
                while (msgByteBuffer.hasRemaining()) {
                    socketChannel.write(msgByteBuffer);
                }
                msgByteBuffer.clear();
            }
        }
    }

    /**
     * 发送命令
     *
     * @param socketChannel
     * @param command
     * @return
     */
    public static String sendCommand(SocketChannel socketChannel, ICommand command) throws IOException {
        logger.info("发送命令:" + JsonUtils.toJson(command));
        if (StringUtils.isBlank(command.getCommandId())) {
            command.setCommandId(UUID.randomUUID().toString());
        }
        if (StringUtils.isBlank(command.getSessionId())) {
            command.setSessionId(UUID.randomUUID().toString());
        }
        SocketChannelUtils.write(socketChannel, SerializeUtils.encode(command));
        return command.getCommandId();
    }

    public static byte[] readBytes(SocketChannel socketChannel, int length) {
        synchronized (socketChannel) {
            try {
                byte[] bytes = new byte[length];
                ByteBuffer msgByteBuffer = ByteBuffer.allocate(READ_WRITE_BUFFER_BYTES);
                int offset = 0;
                while (true) {
                    int readCount = socketChannel.read(msgByteBuffer);
                    msgByteBuffer.flip();
                    msgByteBuffer.get(bytes, offset, readCount);
                    msgByteBuffer.clear();
                    offset = offset + readCount;
                    if (offset == length) {
                        break;
                    }
                }
                return bytes;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
