package arthas;

import com.alibaba.bytekit.utils.IOUtils;
import com.gy4j.jvm.eye.core.util.FileUtils;
import com.gy4j.jvm.eye.core.util.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import static com.gy4j.jvm.eye.core.constant.EyeConstants.OUTPUT_DIRECTION;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class VmTool {
    private static final Logger logger = LoggerFactory.getLogger(VmTool.class);

    private static VmTool instance;

    private static String libName = null;

    static {
        if (OSUtils.isMac()) {
            libName = "libArthasJniLibrary.dylib";
        }
        if (OSUtils.isLinux()) {
            libName = "libArthasJniLibrary-x64.so";
        }
        if (OSUtils.isWindows()) {
            libName = "libArthasJniLibrary-x64.dll";
        }
    }

    private VmTool() {
    }

    public static synchronized VmTool getInstance() {
        if (instance != null) {
            return instance;
        }
        String libPath = "";
        File libFile = Paths.get(OUTPUT_DIRECTION.getAbsolutePath(), libName).toFile();
        if (libFile.exists()) {
            libPath = libFile.getAbsolutePath();
        } else {
            FileOutputStream tmpLibOutputStream = null;
            InputStream libInputStream = null;
            try {
                tmpLibOutputStream = FileUtils.openOutputStream(libFile, false);
                if (libName != null) {
                    try {
                        libInputStream = VmTool.class.getResourceAsStream("/lib" + File.separator + libName);
                    } catch (Throwable e) {
                        logger.error("can not find VmTool so", e);
                    }
                }
                IOUtils.copy(libInputStream, tmpLibOutputStream);
                logger.debug("copy to {}", libFile);
                libPath = libFile.getAbsolutePath();
            } catch (Throwable e) {
                logger.error("try to copy lib error! libName: {}", libName, e);
            } finally {
                IOUtils.close(libInputStream);
                IOUtils.close(tmpLibOutputStream);
            }
        }

        // 系统加载本地方法库
        System.load(libPath);
        instance = new VmTool();
        return instance;
    }

    private static synchronized native void forceGc0();

    /**
     * 获取某个class在jvm中当前所有存活实例
     */
    private static synchronized native <T> T[] getInstances0(Class<T> klass, int limit);

    /**
     * 统计某个class在jvm中当前所有存活实例的总占用内存，单位：Byte
     */
    private static synchronized native long sumInstanceSize0(Class<?> klass);

    /**
     * 获取某个实例的占用内存，单位：Byte
     */
    private static native long getInstanceSize0(Object instance);

    /**
     * 统计某个class在jvm中当前所有存活实例的总个数
     */
    private static synchronized native long countInstances0(Class<?> klass);

    /**
     * 获取所有已加载的类
     *
     * @param klass 这个参数必须是 Class.class
     * @return
     */
    private static synchronized native Class<?>[] getAllLoadedClasses0(Class<?> klass);

    public <T> T[] getInstances(Class<T> klass, int limit) {
        if (limit == 0) {
            throw new IllegalArgumentException("limit can not be 0");
        }
        return getInstances0(klass, limit);
    }
}
