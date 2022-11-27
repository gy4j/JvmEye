package com.gy4j.jvm.eye.core.util;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public final class ObjectUtils {
    private ObjectUtils() {

    }

    /**
     * 获取obj的描述
     *
     * @param obj
     * @param deep
     * @param sizeLimit
     * @param isJson
     * @return
     */
    public static String getObjectInfo(Object obj, int sizeLimit, int deep, boolean isJson) {
        String info = "";
        if (isJson) {
            try {
                info = JsonUtils.toJson(obj);
                return info;
            } catch (Exception ex) {
                info = "parse json error: " + ex.getMessage() + ".\n";
            }
        }
        info = info + getObjectInfo(obj, sizeLimit, deep);
        return info;
    }

    /**
     * 获取obj的描述
     *
     * @param obj
     * @param sizeLimit
     * @param deep
     * @return
     */
    public static String getObjectInfo(Object obj, int sizeLimit, int deep) {
        ObjectRender objectRender = new ObjectRender(sizeLimit, deep);
        return objectRender.getObjectInfo(obj);
    }
}
