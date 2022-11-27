package com.gy4j.jvm.eye.core.listener;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ThreadLocalWatch {
    private final ThreadLocal<LongStack> timestampRef = new ThreadLocal<LongStack>() {
        @Override
        protected LongStack initialValue() {
            return new LongStack(1024 * 4);
        }
    };

    public long start() {
        final long timestamp = System.nanoTime();
        timestampRef.get().push(timestamp);
        return timestamp;
    }

    public long cost() {
        return (System.nanoTime() - timestampRef.get().pop());
    }

    public double costInMillis() {
        return (System.nanoTime() - timestampRef.get().pop()) / 1000000.0;
    }

    /**
     * 一个特殊的stack，为了追求效率，避免扩容。
     * 因为这个stack的push/pop 并不一定成对调用，比如可能push执行了，但是后面的流程被中断了，pop没有被执行。
     * 如果不固定大小，一直增长的话，极端情况下可能应用有内存问题。
     * 如果到达容量，pos会重置，循环存储数据。所以使用这个Stack如果在极端情况下统计的数据会不准确，只用于monitor/watch等命令的计时。
     */
    static class LongStack {
        private long[] array;
        private int pos = 0;
        private int cap;

        public LongStack(int maxSize) {
            array = new long[maxSize];
            cap = array.length;
        }

        public int size() {
            return pos;
        }

        public void push(long value) {
            if (pos < cap) {
                array[pos++] = value;
            } else {
                // if array is full, reset pos
                pos = 0;
                array[pos++] = value;
            }
        }

        public long pop() {
            if (pos > 0) {
                pos--;
                return array[pos];
            } else {
                pos = cap;
                pos--;
                return array[pos];
            }
        }
    }
}
