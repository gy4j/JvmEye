package com.gy4j.jvm.eye.core.command.jvm.vo;

import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class RuntimeInfoVO {
    private String osName;
    private String osVersion;
    private String javaVersion;
    private String javaHome;
    private double systemLoadAverage;
    private int processors;
    private long uptime;
    private long timestamp;
}
