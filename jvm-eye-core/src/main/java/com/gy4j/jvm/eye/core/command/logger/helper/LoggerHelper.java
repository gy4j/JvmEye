package com.gy4j.jvm.eye.core.command.logger.helper;

/**
 * @author gy4j
 */
public interface LoggerHelper {
    String clazz = "class";
    String className = "className";
    String classLoader = "classLoader";
    String classLoaderHash = "classLoaderHash";
    String codeSource = "codeSource";

    // logger info
    String level = "level";
    String effectiveLevel = "effectiveLevel";

    // log4j2 only
    String config = "config";

    // type boolean
    String additivity = "additivity";
    String appenders = "appenders";

    // appender info
    String name = "name";
    String file = "file";
    String blocking = "blocking";

    // type List<String>
    String appenderRef = "appenderRef";
    String target = "target";
}
