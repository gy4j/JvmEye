package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.constant.EyeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public final class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {

    }

    /**
     * 开启文件的写入流
     *
     * @param file
     * @param append
     * @return
     * @throws IOException
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    /**
     * 导出class到文件
     *
     * @param clazz
     * @param data
     */
    public static File dumpClassIfNecessary(Class<?> clazz, byte[] data) {
        String className = clazz.getName();
        ClassLoader classLoader = clazz.getClassLoader();

        // 创建类所在的包路径
        File dumpDir = EyeConstants.OUTPUT_DIRECTION;

        if (!dumpDir.mkdirs() && !dumpDir.exists()) {
            logger.warn("create dump directory:{} failed.", dumpDir.getAbsolutePath());
            return null;
        }

        String fileName;
        if (classLoader != null) {
            fileName = classLoader.getClass().getName() + "-" + Integer.toHexString(classLoader.hashCode()) +
                    File.separator + className.replace(".", File.separator) + ".class";
        } else {
            fileName = className.replace(".", File.separator) + ".class";
        }

        File dumpClassFile = new File(dumpDir, fileName);

        try {
            // 将类字节码写入文件
            FileUtils.writeByteArrayToFile(dumpClassFile, data);
            return dumpClassFile;
        } catch (IOException e) {
            logger.warn("dump class:{} to file {} failed.", className, dumpClassFile);
        }
        return null;
    }

    /**
     * 覆盖写文件
     *
     * @param file
     * @param data
     * @throws IOException
     */
    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }

    /**
     * 写文件
     *
     * @param file
     * @param data
     * @param append
     * @throws IOException
     */
    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            out.write(data);
            out.close();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
            }
        }
    }

}
