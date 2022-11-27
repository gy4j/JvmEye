package com.gy4j.jvm.eye.core.command.logger.vo;

import com.gy4j.jvm.eye.core.command.logger.LoggerInfoCommand;
import lombok.Data;

import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class LoggerInfoVO {
    private LoggerInfoCommand.LoggerType loggerType;
    private String className;
    private String classLoader;
    private String classLoaderHash;
    private String codeSource;

    // logger info
    private String level;
    private String effectiveLevel;

    // log4j2 only
    private String config;

    // type boolean
    private String additivity;
    private List<AppenderInfoVO> appenders;
}
