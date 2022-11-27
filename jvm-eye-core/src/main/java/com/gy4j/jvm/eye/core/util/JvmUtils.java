package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.command.jvm.vo.GcInfoVO;
import com.gy4j.jvm.eye.core.command.jvm.vo.MemoryInfoVO;
import com.gy4j.jvm.eye.core.command.jvm.vo.RuntimeInfoVO;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26-16:53
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class JvmUtils {
    public static List<MemoryInfoVO> getMemoryInfos() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        List<MemoryInfoVO> memoryInfos = new ArrayList<MemoryInfoVO>();

        // heap
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        memoryInfos.add(createMemoryInfoVO(MemoryInfoVO.TYPE_HEAP, MemoryInfoVO.TYPE_HEAP, heapMemoryUsage));
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = getUsage(poolMXBean);
                if (usage != null) {
                    String poolName = StringUtils.beautifyName(poolMXBean.getName());
                    memoryInfos.add(createMemoryInfoVO(MemoryInfoVO.TYPE_HEAP, poolName, usage));
                }
            }
        }
        // non-heap
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        memoryInfos.add(createMemoryInfoVO(MemoryInfoVO.TYPE_NON_HEAP, MemoryInfoVO.TYPE_NON_HEAP, nonHeapMemoryUsage));
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.NON_HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = getUsage(poolMXBean);
                if (usage != null) {
                    String poolName = StringUtils.beautifyName(poolMXBean.getName());
                    memoryInfos.add(createMemoryInfoVO(MemoryInfoVO.TYPE_NON_HEAP, poolName, usage));
                }
            }
        }
        addBufferPoolMemoryInfo(memoryInfos);
        return memoryInfos;
    }

    private static MemoryUsage getUsage(MemoryPoolMXBean memoryPoolMXBean) {
        try {
            return memoryPoolMXBean.getUsage();
        } catch (InternalError e) {
            // Defensive for potential InternalError with some specific JVM options. Based on its Javadoc,
            // MemoryPoolMXBean.getUsage() should return null, not throwing InternalError, so it seems to be a JVM bug.
            return null;
        }
    }

    private static void addBufferPoolMemoryInfo(List<MemoryInfoVO> memoryInfos) {
        try {
            @SuppressWarnings("rawtypes")
            Class bufferPoolMXBeanClass = Class.forName("java.lang.management.BufferPoolMXBean");
            @SuppressWarnings("unchecked")
            List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(bufferPoolMXBeanClass);
            for (BufferPoolMXBean mbean : bufferPoolMXBeans) {
                long used = mbean.getMemoryUsed();
                long total = mbean.getTotalCapacity();
                memoryInfos.add(new MemoryInfoVO(MemoryInfoVO.TYPE_BUFFER_POOL, mbean.getName(), used, total, Long.MIN_VALUE));
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
    }

    private static MemoryInfoVO createMemoryInfoVO(String type, String name, MemoryUsage memoryUsage) {
        return new MemoryInfoVO(type, name, memoryUsage.getUsed(), memoryUsage.getCommitted(), memoryUsage.getMax());
    }

    public static RuntimeInfoVO getRuntimeInfo() {
        RuntimeInfoVO runtimeInfo = new RuntimeInfoVO();
        runtimeInfo.setOsName(System.getProperty("os.name"));
        runtimeInfo.setOsVersion(System.getProperty("os.version"));
        runtimeInfo.setJavaVersion(System.getProperty("java.version"));
        runtimeInfo.setJavaHome(System.getProperty("java.home"));
        runtimeInfo.setSystemLoadAverage(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
        runtimeInfo.setProcessors(Runtime.getRuntime().availableProcessors());
        runtimeInfo.setUptime(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        runtimeInfo.setTimestamp(System.currentTimeMillis());
        return runtimeInfo;
    }

    public static List<GcInfoVO> getGcInfos() {
        List<GcInfoVO> gcInfos = new ArrayList<GcInfoVO>();
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcMXBean : garbageCollectorMxBeans) {
            String name = gcMXBean.getName();
            gcInfos.add(new GcInfoVO(StringUtils.beautifyName(name),
                    gcMXBean.getCollectionCount(), gcMXBean.getCollectionTime()));
        }
        return gcInfos;
    }
}
