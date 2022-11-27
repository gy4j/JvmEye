package com.gy4j.jvm.eye.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SeqUtils {

    static char[] CHAR_ARR = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9'};

    /**
     * 生成序号ID(20位)
     *
     * @return
     */
    public static String getSeq() {
        String currentTimeMils = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        currentTimeMils += getRandomStr(3);
        return currentTimeMils;
    }

    /**
     * 获取随机串
     *
     * @param count 位数
     * @return
     */
    public static String getRandomStr(int count) {
        String strEnsure = "";
        for (int i = 0; i < count; ++i) {
            strEnsure += CHAR_ARR[(int) (CHAR_ARR.length * Math.random())];
        }
        return strEnsure;
    }
}
