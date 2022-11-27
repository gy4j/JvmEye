package com.gy4j.jvm.eye.core.util;

import com.gy4j.jvm.eye.core.command.thread.vo.ThreadSampleInfoVO;
import sun.management.HotspotThreadMBean;
import sun.management.ManagementFactoryHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26-17:07
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ThreadSampler {
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static HotspotThreadMBean hotspotThreadMBean;
    private static boolean hotspotThreadMBeanEnable = true;

    private Map<ThreadSampleInfoVO, Long> lastCpuTimes = new HashMap<ThreadSampleInfoVO, Long>();

    private long lastSampleTimeNanos;

    public List<ThreadSampleInfoVO> sample(Collection<ThreadSampleInfoVO> originThreads) {
        List<ThreadSampleInfoVO> threads = new ArrayList<ThreadSampleInfoVO>();
        for (ThreadSampleInfoVO threadInfo : originThreads) {
            threads.add(threadInfo);
        }

        // Sample CPU
        if (lastCpuTimes.isEmpty()) {
            lastSampleTimeNanos = System.nanoTime();
            for (ThreadSampleInfoVO thread : threads) {
                if (thread.getId() > 0) {
                    long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                    lastCpuTimes.put(thread, cpu);
                    thread.setTime(cpu / 1000000);
                }
            }

            // add internal threads
            Map<String, Long> internalThreadCpuTimes = getInternalThreadCpuTimes();
            if (internalThreadCpuTimes != null) {
                for (Map.Entry<String, Long> entry : internalThreadCpuTimes.entrySet()) {
                    String key = entry.getKey();
                    ThreadSampleInfoVO thread = createThreadSampleInfoVO(key);
                    thread.setTime(entry.getValue() / 1000000);
                    threads.add(thread);
                    lastCpuTimes.put(thread, entry.getValue());
                }
            }

            //sort by time
            Collections.sort(threads, new Comparator<ThreadSampleInfoVO>() {
                @Override
                public int compare(ThreadSampleInfoVO o1, ThreadSampleInfoVO o2) {
                    long l1 = o1.getTime();
                    long l2 = o2.getTime();
                    if (l1 < l2) {
                        return 1;
                    } else if (l1 > l2) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            return threads;
        }

        // Resample
        long newSampleTimeNanos = System.nanoTime();
        Map<ThreadSampleInfoVO, Long> newCpuTimes = new HashMap<ThreadSampleInfoVO, Long>(threads.size());
        for (ThreadSampleInfoVO thread : threads) {
            if (thread.getId() > 0) {
                long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                newCpuTimes.put(thread, cpu);
            }
        }
        // internal threads
        Map<String, Long> newInternalThreadCpuTimes = getInternalThreadCpuTimes();
        if (newInternalThreadCpuTimes != null) {
            for (Map.Entry<String, Long> entry : newInternalThreadCpuTimes.entrySet()) {
                ThreadSampleInfoVO ThreadSampleInfoVO = createThreadSampleInfoVO(entry.getKey());
                threads.add(ThreadSampleInfoVO);
                newCpuTimes.put(ThreadSampleInfoVO, entry.getValue());
            }
        }

        // Compute delta time
        final Map<ThreadSampleInfoVO, Long> deltas = new HashMap<ThreadSampleInfoVO, Long>(threads.size());
        for (ThreadSampleInfoVO thread : newCpuTimes.keySet()) {
            Long t = lastCpuTimes.get(thread);
            if (t == null) {
                t = 0L;
            }
            long time1 = t;
            long time2 = newCpuTimes.get(thread);
            if (time1 == -1) {
                time1 = time2;
            } else if (time2 == -1) {
                time2 = time1;
            }
            long delta = time2 - time1;
            deltas.put(thread, delta);
        }

        long sampleIntervalNanos = newSampleTimeNanos - lastSampleTimeNanos;

        // Compute cpu usage
        final HashMap<ThreadSampleInfoVO, Double> cpuUsages = new HashMap<ThreadSampleInfoVO, Double>(threads.size());
        for (ThreadSampleInfoVO thread : threads) {
            double cpu = sampleIntervalNanos == 0 ? 0 : (Math.rint(deltas.get(thread) * 10000.0 / sampleIntervalNanos) / 100.0);
            cpuUsages.put(thread, cpu);
        }

        // Sort by CPU time : should be a rendering hint...
        Collections.sort(threads, new Comparator<ThreadSampleInfoVO>() {
            public int compare(ThreadSampleInfoVO o1, ThreadSampleInfoVO o2) {
                long l1 = deltas.get(o1);
                long l2 = deltas.get(o2);
                if (l1 < l2) {
                    return 1;
                } else if (l1 > l2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (ThreadSampleInfoVO thread : threads) {
            //nanos to mills
            long timeMills = newCpuTimes.get(thread) / 1000000;
            long deltaTime = deltas.get(thread) / 1000000;
            double cpu = cpuUsages.get(thread);

            thread.setCpu(cpu);
            thread.setTime(timeMills);
            thread.setDeltaTime(deltaTime);
        }
        lastCpuTimes = newCpuTimes;
        lastSampleTimeNanos = newSampleTimeNanos;

        return threads;
    }

    private Map<String, Long> getInternalThreadCpuTimes() {
        if (hotspotThreadMBeanEnable) {
            try {
                if (hotspotThreadMBean == null) {
                    hotspotThreadMBean = ManagementFactoryHelper.getHotspotThreadMBean();
                }
                return hotspotThreadMBean.getInternalThreadCpuTimes();
            } catch (Throwable e) {
                //ignore ex
                hotspotThreadMBeanEnable = false;
            }
        }
        return null;
    }

    private ThreadSampleInfoVO createThreadSampleInfoVO(String name) {
        ThreadSampleInfoVO ThreadSampleInfoVO = new ThreadSampleInfoVO();
        ThreadSampleInfoVO.setId(-1);
        ThreadSampleInfoVO.setName(name);
        ThreadSampleInfoVO.setPriority(-1);
        ThreadSampleInfoVO.setDaemon(true);
        ThreadSampleInfoVO.setInterrupted(false);
        return ThreadSampleInfoVO;
    }

    public void pause(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
