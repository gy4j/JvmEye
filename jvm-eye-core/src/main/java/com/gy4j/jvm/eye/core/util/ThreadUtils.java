package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.command.enhance.model.node.ThreadNode;
import com.gy4j.jvm.eye.core.command.thread.vo.ThreadSampleInfoVO;
import com.gy4j.jvm.eye.core.constant.EyeConstants;
import com.gy4j.jvm.eye.core.spy.SpyAPI;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26-16:40
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ThreadUtils {
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    /**
     * 获取线程节点
     *
     * @param currentThread
     * @return
     */
    public static ThreadNode getThreadNode(Thread currentThread) {
        ThreadNode threadNode = new ThreadNode();
        threadNode.setThreadId(currentThread.getId());
        threadNode.setThreadName(currentThread.getName());
        threadNode.setDaemon(currentThread.isDaemon());
        threadNode.setPriority(currentThread.getPriority());
        threadNode.setClassLoader(getClassLoaderName(currentThread));
        return threadNode;
    }

    /**
     * 获取线程的累加器名称
     *
     * @param currentThread
     * @return
     */
    public static String getClassLoaderName(Thread currentThread) {
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        if (null == contextClassLoader) {
            return EyeConstants.NONE;
        } else {
            return contextClassLoader.getClass().getName() + "@" + Integer.toHexString(contextClassLoader.hashCode());
        }
    }

    /**
     * </pre>
     * java.lang.Thread.getStackTrace(Thread.java:1559),
     * com.taobao.arthas.core.util.ThreadUtil.getThreadStack(ThreadUtil.java:349),
     * com.taobao.arthas.core.command.monitor200.StackAdviceListener.before(StackAdviceListener.java:33),
     * com.taobao.arthas.core.advisor.AdviceListenerAdapter.before(AdviceListenerAdapter.java:49),
     * com.taobao.arthas.core.advisor.SpyImpl.atEnter(SpyImpl.java:42),
     * java.arthas.SpyAPI.atEnter(SpyAPI.java:40),
     * demo.MathGame.print(MathGame.java), demo.MathGame.run(MathGame.java:25),
     * demo.MathGame.main(MathGame.java:16)
     * </pre>
     */
    private static int MAGIC_STACK_DEPTH = 0;

    /**
     * 找到SpyAPI的深度
     *
     * @param stackTraceElementArray
     * @return
     */
    public static int findTheSpyAPIDepth(StackTraceElement[] stackTraceElementArray) {
        if (MAGIC_STACK_DEPTH > 0) {
            return MAGIC_STACK_DEPTH;
        }
        if (MAGIC_STACK_DEPTH > stackTraceElementArray.length) {
            return 0;
        }
        for (int i = 0; i < stackTraceElementArray.length; ++i) {
            if (SpyAPI.class.getName().equals(stackTraceElementArray[i].getClassName())) {
                MAGIC_STACK_DEPTH = i + 1;
                break;
            }
        }
        return MAGIC_STACK_DEPTH;
    }

    /**
     * 获取thread的顶层ThreadGroup
     *
     * @return
     */
    private static ThreadGroup getRoot() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = group.getParent()) != null) {
            group = parent;
        }
        return group;
    }

    /**
     * 获取所有线程
     */
    public static List<ThreadSampleInfoVO> getThreads() {
        return getThreads(null);
    }

    /**
     * 获取指定ID所有线程
     */
    public static List<ThreadSampleInfoVO> getThreads(long[] ids) {
        ThreadGroup root = getRoot();
        Thread[] threads = new Thread[root.activeCount()];
        while (root.enumerate(threads, true) == threads.length) {
            threads = new Thread[threads.length * 2];
        }
        List<ThreadSampleInfoVO> list = new ArrayList<ThreadSampleInfoVO>(threads.length);
        for (Thread thread : threads) {
            if (thread != null && (ids == null || ArrayUtils.isInArray(ids, thread.getId()))) {
                ThreadSampleInfoVO ThreadSampleInfoVO = createThreadVO(thread);
                list.add(ThreadSampleInfoVO);
            }
        }
        return list;
    }

    /**
     * 基于Thread创建ThreadInfoVO
     *
     * @param thread
     * @return
     */
    private static ThreadSampleInfoVO createThreadVO(Thread thread) {
        ThreadGroup group = thread.getThreadGroup();
        ThreadSampleInfoVO threadVO = new ThreadSampleInfoVO();
        threadVO.setId(thread.getId());
        threadVO.setName(thread.getName());
        threadVO.setGroup(group == null ? "" : group.getName());
        threadVO.setPriority(thread.getPriority());
        threadVO.setState(thread.getState());
        threadVO.setInterrupted(thread.isInterrupted());
        threadVO.setDaemon(thread.isDaemon());
        return threadVO;
    }

    /**
     * 获取所有的线程列表
     *
     * @return
     */
    public static List<ThreadSampleInfoVO> findAllThreadInfos(long sampleInterval, boolean lockedMonitors, boolean lockedSynchronizers) {
        List<ThreadSampleInfoVO> sampleThreads = getSampleThreads(sampleInterval);

        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(lockedMonitors, lockedSynchronizers);

        resetThreadInfo(threadInfos, sampleThreads);
        return sampleThreads;
    }

    /**
     * 根据ThreadInfo更新ThreadSampleInfoVO信息
     *
     * @param threadInfos
     * @param threadInfoList
     */
    private static void resetThreadInfo(ThreadInfo[] threadInfos, List<ThreadSampleInfoVO> threadInfoList) {
        for (ThreadSampleInfoVO threadWithoutDetail : threadInfoList) {
            ThreadInfo threadInfo = findThreadInfoById(threadInfos, threadWithoutDetail.getId());
            threadWithoutDetail.initThreadInfo(threadInfo);
        }
    }

    /**
     * 根据id从threadInfos找ThreadInfo
     *
     * @param threadInfos
     * @param id
     * @return
     */
    private static ThreadInfo findThreadInfoById(ThreadInfo[] threadInfos, long id) {
        for (int i = 0; i < threadInfos.length; i++) {
            ThreadInfo threadInfo = threadInfos[i];
            if (threadInfo.getThreadId() == id) {
                return threadInfo;
            }
        }
        return null;
    }


    /**
     * 获取间隔采样的线程列表
     *
     * @return
     */
    public static List<ThreadSampleInfoVO> getSampleThreads(long sampleInterval) {
        ThreadSampler threadSampler = new ThreadSampler();
        threadSampler.sample(ThreadUtils.getThreads());
        threadSampler.pause(sampleInterval);
        List<ThreadSampleInfoVO> sampleThreads = threadSampler.sample(ThreadUtils.getThreads());
        return sampleThreads;
    }
}
