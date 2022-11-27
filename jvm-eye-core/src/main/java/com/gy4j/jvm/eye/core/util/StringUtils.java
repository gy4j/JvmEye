package com.gy4j.jvm.eye.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public final class StringUtils {
    private StringUtils() {

    }

    public static boolean isBlank(String val) {
        return val == null || val.trim().length() == 0;
    }

    // print|(ILjava/util/List;)V
    public static String[] splitMethodInfo(String methodInfo) {
        int index = methodInfo.indexOf('|');
        return new String[]{methodInfo.substring(0, index), methodInfo.substring(index + 1)};
    }

    // demo/MathGame|primeFactors|(I)Ljava/util/List;|24
    public static String[] splitInvokeInfo(String invokeInfo) {
        int index1 = invokeInfo.indexOf('|');
        int index2 = invokeInfo.indexOf('|', index1 + 1);
        int index3 = invokeInfo.indexOf('|', index2 + 1);
        return new String[]{invokeInfo.substring(0, index1), invokeInfo.substring(index1 + 1, index2),
                invokeInfo.substring(index2 + 1, index3), invokeInfo.substring(index3 + 1)};
    }

    public static List<String> toLines(String text) {
        List<String> result = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(text));
        try {
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (IOException exc) {
            // quit
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return result;
    }

    public static String beautifyName(String name) {
        return name.replace(' ', '_').toLowerCase();
    }

    public static String getStackTrace(StackTraceElement[] stackTraceElements) {
        StringBuffer sb = new StringBuffer();
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return sb.toString();
        }
        sb.append("@").append(stackTraceElements[0].getClassName()).append(".")
                .append(stackTraceElements[0].getMethodName()).append("()\n");
        int skip = 1;
        for (int index = skip; index < stackTraceElements.length; index++) {
            StackTraceElement ste = stackTraceElements[index];
            sb.append("        at ")
                    .append(ste.getClassName())
                    .append(".")
                    .append(ste.getMethodName())
                    .append("(")
                    .append(ste.getFileName())
                    .append(":")
                    .append(ste.getLineNumber())
                    .append(")\n");
        }
        return sb.toString();
    }
}
