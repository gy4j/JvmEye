package com.gy4j.jvm.eye.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public final class JsonUtils {
    private JsonUtils() {
    }

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static <T> T parseJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
