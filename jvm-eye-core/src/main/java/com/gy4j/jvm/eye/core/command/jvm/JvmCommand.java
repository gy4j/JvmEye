package com.gy4j.jvm.eye.core.command.jvm;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.JvmResponse;
import com.gy4j.jvm.eye.core.command.jvm.vo.JvmInfoVO;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JsonUtils;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class JvmCommand extends AbstractCommand {


    @Override
    public IResponse executeForResponse(IClient client) {
        JvmResponse jvmResponse = new JvmResponse();
        JvmInfoVO jvmInfoVO = new JvmInfoVO();
        addRuntimeInfo(jvmInfoVO.getRuntimeInfo());
        addClassLoading(jvmInfoVO.getClassLoadingInfo());
        addCompilation(jvmInfoVO.getCompilationInfo());
        addGarbageCollectors(jvmInfoVO.getGarbageCollectorInfo());
        addMemoryManagers(jvmInfoVO.getMemoryManagerInfo());
        addMemory(jvmInfoVO.getMemoryInfo());
        addOperatingSystem(jvmInfoVO.getOperatingSystemInfo());
        addThread(jvmInfoVO.getThreadInfo());
        addFileDescriptor(jvmInfoVO.getFileDescriptorInfo());
        jvmResponse.setJvmInfo(jvmInfoVO);
        return jvmResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return JvmResponse.class;
    }

    private void addFileDescriptor(Map<String, String> fileDescriptorInfo) {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        fileDescriptorInfo.put("MAX-FILE-DESCRIPTOR-COUNT", invokeFileDescriptor(operatingSystemMXBean, "getMaxFileDescriptorCount") + "");
        fileDescriptorInfo.put("OPEN-FILE-DESCRIPTOR-COUNT", invokeFileDescriptor(operatingSystemMXBean, "getOpenFileDescriptorCount") + "");
    }

    private long invokeFileDescriptor(OperatingSystemMXBean os, String name) {
        try {
            final Method method = os.getClass().getDeclaredMethod(name);
            method.setAccessible(true);
            return (Long) method.invoke(os);
        } catch (Exception e) {
            return -1;
        }
    }

    private void addRuntimeInfo(Map<String, String> runtimeInfo) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String bootClassPath = "";
        try {
            bootClassPath = runtimeMXBean.getBootClassPath();
        } catch (Exception e) {
            // under jdk9 will throw UnsupportedOperationException, ignore
        }
        runtimeInfo.put("MACHINE-NAME", runtimeMXBean.getName());
        runtimeInfo.put("JVM-START-TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(runtimeMXBean.getStartTime())));
        runtimeInfo.put("MANAGEMENT-SPEC-VERSION", runtimeMXBean.getManagementSpecVersion());
        runtimeInfo.put("SPEC-NAME", runtimeMXBean.getSpecName());
        runtimeInfo.put("SPEC-VENDOR", runtimeMXBean.getSpecVendor());
        runtimeInfo.put("SPEC-VERSION", runtimeMXBean.getSpecVersion());
        runtimeInfo.put("VM-NAME", runtimeMXBean.getVmName());
        runtimeInfo.put("VM-VENDOR", runtimeMXBean.getVmVendor());
        runtimeInfo.put("VM-VERSION", runtimeMXBean.getVmVersion());
        runtimeInfo.put("INPUT-ARGUMENTS", JsonUtils.toJson(runtimeMXBean.getInputArguments()));
        runtimeInfo.put("CLASS-PATH", runtimeMXBean.getClassPath());
        runtimeInfo.put("BOOT-CLASS-PATH", bootClassPath);
        runtimeInfo.put("LIBRARY-PATH", runtimeMXBean.getLibraryPath());
    }

    private void addClassLoading(Map<String, String> classLoadingInfo) {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        classLoadingInfo.put("LOADED-CLASS-COUNT", classLoadingMXBean.getLoadedClassCount() + "");
        classLoadingInfo.put("TOTAL-LOADED-CLASS-COUNT", classLoadingMXBean.getTotalLoadedClassCount() + "");
        classLoadingInfo.put("UNLOADED-CLASS-COUNT", classLoadingMXBean.getUnloadedClassCount() + "");
        classLoadingInfo.put("IS-VERBOSE", classLoadingMXBean.isVerbose() + "");
    }

    private void addCompilation(Map<String, String> compilationInfo) {
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        if (compilationMXBean == null) {
            return;
        }
        compilationInfo.put("NAME", compilationMXBean.getName());
        if (compilationMXBean.isCompilationTimeMonitoringSupported()) {
            compilationInfo.put("TOTAL-COMPILE-TIME", compilationMXBean.getTotalCompilationTime() + "");
        }
    }

    private void addGarbageCollectors(Map<String, String> garbageCollectorInfo) {
        Collection<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        if (garbageCollectorMXBeans.isEmpty()) {
            return;
        }
        for (GarbageCollectorMXBean gcMXBean : garbageCollectorMXBeans) {
            Map<String, Object> gcInfo = new LinkedHashMap<String, Object>();
            gcInfo.put("name", gcMXBean.getName());
            gcInfo.put("collectionCount", gcMXBean.getCollectionCount());
            gcInfo.put("collectionTime", gcMXBean.getCollectionTime());
            garbageCollectorInfo.put(gcMXBean.getName(), JsonUtils.toJson(gcInfo));
        }
    }

    private void addMemoryManagers(Map<String, String> memoryManagerInfo) {
        Collection<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        if (memoryManagerMXBeans.isEmpty()) {
            return;
        }
        for (final MemoryManagerMXBean memoryManagerMXBean : memoryManagerMXBeans) {
            if (memoryManagerMXBean.isValid()) {
                final String name = memoryManagerMXBean.isValid()
                        ? memoryManagerMXBean.getName()
                        : memoryManagerMXBean.getName() + "(Invalid)";
                memoryManagerInfo.put(name, JsonUtils.toJson(memoryManagerMXBean.getMemoryPoolNames()));
            }
        }
    }

    private void addMemory(Map<String, String> memoryInfo) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        Map<String, Object> heapMemoryInfo = getMemoryUsageInfo("heap", heapMemoryUsage);
        memoryInfo.put("HEAP-MEMORY-USAGE", JsonUtils.toJson(heapMemoryInfo));

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        Map<String, Object> nonHeapMemoryInfo = getMemoryUsageInfo("non_heap", nonHeapMemoryUsage);
        memoryInfo.put("NO-HEAP-MEMORY-USAGE", JsonUtils.toJson(nonHeapMemoryInfo));
        memoryInfo.put("PENDING-FINALIZE-COUNT", memoryMXBean.getObjectPendingFinalizationCount() + "");
    }

    private Map<String, Object> getMemoryUsageInfo(String name, MemoryUsage heapMemoryUsage) {
        Map<String, Object> memoryInfo = new LinkedHashMap<String, Object>();
        memoryInfo.put("name", name);
        memoryInfo.put("init", heapMemoryUsage.getInit());
        memoryInfo.put("used", heapMemoryUsage.getUsed());
        memoryInfo.put("committed", heapMemoryUsage.getCommitted());
        memoryInfo.put("max", heapMemoryUsage.getMax());
        return memoryInfo;
    }

    private void addOperatingSystem(Map<String, String> operatingSystemInfo) {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        operatingSystemInfo.put("OS", operatingSystemMXBean.getName());
        operatingSystemInfo.put("ARCH", operatingSystemMXBean.getArch());
        operatingSystemInfo.put("PROCESSORS-COUNT", operatingSystemMXBean.getAvailableProcessors() + "");
        operatingSystemInfo.put("LOAD-AVERAGE", operatingSystemMXBean.getSystemLoadAverage() + "");
        operatingSystemInfo.put("VERSION", operatingSystemMXBean.getVersion());
    }

    private void addThread(Map<String, String> threadInfo) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadInfo.put("COUNT", threadMXBean.getThreadCount() + "");
        threadInfo.put("DAEMON-COUNT", threadMXBean.getDaemonThreadCount() + "");
        threadInfo.put("PEAK-COUNT", threadMXBean.getPeakThreadCount() + "");
        threadInfo.put("STARTED-COUNT", threadMXBean.getTotalStartedThreadCount() + "");
        threadInfo.put("DEADLOCK-COUNT", getDeadlockedThreadsCount(threadMXBean) + "");
    }

    private int getDeadlockedThreadsCount(ThreadMXBean threads) {
        final long[] ids = threads.findDeadlockedThreads();
        if (ids == null) {
            return 0;
        } else {
            return ids.length;
        }
    }
}
