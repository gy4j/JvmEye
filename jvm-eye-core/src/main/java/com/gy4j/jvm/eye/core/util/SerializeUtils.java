package com.gy4j.jvm.eye.core.util;

import java.nio.charset.StandardCharsets;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SerializeUtils {
    private SerializeUtils() {

    }

    /**
     * 编码
     *
     * @param obj
     * @return
     */
    public static byte[] encode(Object obj) {
        String className = obj.getClass().getName();
        byte[] bytes = (className + "#" + JsonUtils.toJson(obj)).getBytes(StandardCharsets.UTF_8);
        return bytes;
    }

    /**
     * 解码
     */
    public static <T> T decode(byte[] msgBytes) {
        try {
            String str = new String(msgBytes, StandardCharsets.UTF_8);
            int index = str.indexOf("#");
            if (index != -1) {
                String className = str.substring(0, index);
                String json = str.substring(index + 1);
                Class<?> clazz = Class.forName(className);
                T obj = (T) JsonUtils.parseJson(json, clazz);
                return obj;
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
