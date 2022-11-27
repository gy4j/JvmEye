package com.gy4j.jvm.eye.core.command.logger;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.logger.helper.Log4j2Helper;
import com.gy4j.jvm.eye.core.command.logger.helper.Log4jHelper;
import com.gy4j.jvm.eye.core.command.logger.helper.LogbackHelper;
import com.gy4j.jvm.eye.core.command.logger.helper.LoggerHelper;
import com.gy4j.jvm.eye.core.command.logger.response.LoggerInfoResponse;
import com.gy4j.jvm.eye.core.command.logger.vo.AppenderInfoVO;
import com.gy4j.jvm.eye.core.command.logger.vo.LoggerInfoVO;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import com.gy4j.jvm.eye.core.util.ClassUtils;
import com.gy4j.jvm.eye.core.util.StringUtils;
import lombok.Data;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class LoggerInfoCommand extends AbstractCommand {
    /**
     * Logger name
     * 为空的时候，只能查询存在appender的logger，日志配置文件里面配置的，比如root
     * 不为空的时候，可以用类的全路径进行查询，比如LoggerInfoCommand
     */
    private String name;
    /**
     * 类加载器hashcode
     */
    private String classLoaderHash;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return LoggerInfoResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        LoggerInfoResponse loggerInfoResponse = new LoggerInfoResponse();
        List<LoggerInfoVO> loggerInfos = new ArrayList<LoggerInfoVO>();
        initLoggerInfos(client.getInstrumentation(), loggerInfos);
        loggerInfoResponse.setLoggerInfos(loggerInfos);
        if (loggerInfos.isEmpty()) {
            return createExceptionResponse("找不到该类的logger信息");
        }
        return loggerInfoResponse;
    }

    public void initLoggerInfos(Instrumentation instrumentation, List<LoggerInfoVO> loggerInfos) {
        Map<ClassLoader, LoggerTypes> classLoaderLoggerMap = new LinkedHashMap<ClassLoader, LoggerTypes>();
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            String className = clazz.getName();
            ClassLoader classLoader = clazz.getClassLoader();
            // if special classloader
            if (!StringUtils.isBlank(this.classLoaderHash)
                    && !this.classLoaderHash.equals(ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader()))) {
                continue;
            }
            if (classLoader != null) {
                LoggerTypes loggerTypes = classLoaderLoggerMap.get(classLoader);
                if (loggerTypes == null) {
                    loggerTypes = new LoggerTypes();
                    classLoaderLoggerMap.put(classLoader, loggerTypes);
                }
                if ("org.apache.log4j.Logger".equals(className)) {
                    loggerTypes.addType(LoggerType.LOG4J);
                } else if ("ch.qos.logback.classic.Logger".equals(className)) {
                    loggerTypes.addType(LoggerType.LOGBACK);
                } else if ("org.apache.logging.log4j.Logger".equals(className)) {
                    loggerTypes.addType(LoggerType.LOG4J2);
                }
            }
        }

        for (Map.Entry<ClassLoader, LoggerTypes> entry : classLoaderLoggerMap.entrySet()) {
            // LoggerHelper可能和需要查询日志级别的类加载器不是同一个，暂时忽略
            // ClassLoader classLoader = entry.getKey();
            LoggerTypes loggerTypes = entry.getValue();

            if (loggerTypes.contains(LoggerType.LOG4J)) {
                Map<String, Map<String, Object>> loggerInfoMap = Log4jHelper.getLoggers(name);
                loggerInfos.addAll(resetLoggerInfoWithClassLoader(loggerInfoMap, LoggerType.LOG4J));
            }

            if (loggerTypes.contains(LoggerType.LOGBACK)) {
                Map<String, Map<String, Object>> loggerInfoMap = LogbackHelper.getLoggers(name);
                loggerInfos.addAll(resetLoggerInfoWithClassLoader(loggerInfoMap, LoggerType.LOGBACK));
            }

            if (loggerTypes.contains(LoggerType.LOG4J2)) {
                Map<String, Map<String, Object>> loggerInfoMap = Log4j2Helper.getLoggers(name);
                loggerInfos.addAll(resetLoggerInfoWithClassLoader(loggerInfoMap, LoggerType.LOG4J2));
            }
        }
    }

    private List<LoggerInfoVO> resetLoggerInfoWithClassLoader(Map<String, Map<String, Object>> loggers, LoggerType loggerType) {
        List<LoggerInfoVO> loggerInfos = new ArrayList<LoggerInfoVO>();
        //expose attributes to json: classloader, classloaderHash
        for (Map<String, Object> loggerInfo : loggers.values()) {
            LoggerInfoVO loggerInfoVO = new LoggerInfoVO();
            Class clazz = (Class) loggerInfo.get(LoggerHelper.clazz);

            loggerInfoVO.setLoggerType(loggerType);

            loggerInfoVO.setClassLoader(getClassLoaderName(clazz.getClassLoader()));
            loggerInfoVO.setClassLoaderHash(ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader()));
            loggerInfoVO.setClassName(ClassUtils.getClassName(clazz));
            loggerInfoVO.setAdditivity(loggerInfo.get(LoggerHelper.additivity) + "");
            loggerInfoVO.setConfig(loggerInfo.get(LoggerHelper.config) + "");
            loggerInfoVO.setCodeSource(loggerInfo.get(LoggerHelper.codeSource) + "");
            loggerInfoVO.setLevel(loggerInfo.get(LoggerHelper.level) + "");
            loggerInfoVO.setEffectiveLevel(loggerInfo.get(LoggerHelper.effectiveLevel) + "");

            List<Map<String, Object>> appenders = (List<Map<String, Object>>) loggerInfo.get(LoggerHelper.appenders);
            List<AppenderInfoVO> appenderInfoList = new ArrayList<AppenderInfoVO>();
            for (Map<String, Object> appenderInfo : appenders) {
                Class appenderClass = (Class) appenderInfo.get(LoggerHelper.clazz);
                if (appenderClass != null) {
                    AppenderInfoVO appenderInfoVO = new AppenderInfoVO();
                    appenderInfoVO.setClassLoader(getClassLoaderName(appenderClass.getClassLoader()));
                    appenderInfoVO.setClassLoaderHash(ClassLoaderUtils.getClassLoaderHash(appenderClass.getClassLoader()));
                    appenderInfoVO.setClassName(ClassUtils.getClassName(appenderClass));

                    appenderInfoVO.setAppenderRef(appenderInfo.get(LoggerHelper.appenders) + "");
                    appenderInfoVO.setBlocking(appenderInfo.get(LoggerHelper.blocking) + "");
                    appenderInfoVO.setFile(appenderInfo.get(LoggerHelper.file) + "");
                    appenderInfoVO.setTarget(appenderInfo.get(LoggerHelper.target) + "");
                    appenderInfoVO.setName(appenderInfo.get(LoggerHelper.name) + "");
                    appenderInfoList.add(appenderInfoVO);
                }
            }
            loggerInfoVO.setAppenders(appenderInfoList);
            loggerInfos.add(loggerInfoVO);
        }
        return loggerInfos;
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        return classLoader == null ? null : classLoader.toString();
    }

    public static enum LoggerType {
        LOG4J, LOGBACK, LOG4J2
    }

    static class LoggerTypes {
        Set<LoggerType> types = new HashSet<LoggerType>();

        public Collection<LoggerType> types() {
            return types;
        }

        public void addType(LoggerType type) {
            types.add(type);
        }

        public boolean contains(LoggerType type) {
            return types.contains(type);
        }
    }
}
