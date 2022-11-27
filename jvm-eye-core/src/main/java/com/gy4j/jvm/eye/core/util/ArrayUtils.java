package com.gy4j.jvm.eye.core.util;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26-17:12
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class ArrayUtils {
    public static final long[] EMPTY_LONG_ARRAY = new long[0];

    public static long[] toPrimitive(final Long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }

    public static boolean isInArray(long[] ids, long id) {
        if (ids == null) {
            return false;
        }
        for (long tId : ids) {
            if (tId == id) {
                return true;
            }
        }
        return false;
    }
}
