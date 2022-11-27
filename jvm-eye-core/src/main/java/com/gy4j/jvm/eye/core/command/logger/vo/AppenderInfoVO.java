package com.gy4j.jvm.eye.core.command.logger.vo;

import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class AppenderInfoVO {
    private String className;
    private String classLoader;
    private String classLoaderHash;
    // appender info
    private String name;
    private String file;
    private String blocking;
    // type List<String>
    private String appenderRef;
    private String target;
}
