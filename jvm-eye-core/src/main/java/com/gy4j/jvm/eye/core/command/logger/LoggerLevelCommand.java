package com.gy4j.jvm.eye.core.command.logger;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.logger.helper.Log4j2Helper;
import com.gy4j.jvm.eye.core.command.logger.helper.Log4jHelper;
import com.gy4j.jvm.eye.core.command.logger.helper.LogbackHelper;
import com.gy4j.jvm.eye.core.command.logger.response.LoggerLevelResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class LoggerLevelCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(LoggerLevelCommand.class);

    /**
     * Logger name
     * 可以为存在appender的logger：比如root
     * 可以为类名的全路径：比如LoggerInfoCommand
     */
    private String name;
    /**
     * 日志级别
     */
    private String level;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return LoggerLevelResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        LoggerLevelResponse response = new LoggerLevelResponse();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(level)) {
            return createExceptionResponse("name and level cannot be null!");
        }
        if (!checkLevel(level)) {
            return createExceptionResponse("level 不合法!");
        }
        boolean result = false;
        try {
            Boolean updateResult = Log4jHelper.updateLevel(name, level);
            if (Boolean.TRUE.equals(updateResult)) {
                result = true;
            }
        } catch (Throwable e) {
            logger.warn("logger command update log4j level error", e);
        }
        try {
            Boolean updateResult = LogbackHelper.updateLevel(name, level);
            if (Boolean.TRUE.equals(updateResult)) {
                result = true;
            }
        } catch (Throwable e) {
            logger.warn("logger command update logback level error", e);
        }

        try {
            Boolean updateResult = Log4j2Helper.updateLevel(name, level);
            if (Boolean.TRUE.equals(updateResult)) {
                result = true;
            }
        } catch (Throwable e) {
            logger.warn("logger command update log4j2 level error", e);
        }
        if (result) {
            response.setMsg("update successful.");
            return response;
        } else {
            return createExceptionResponse("update failed.");
        }
    }

    /**
     * 检查参数level的合法性
     *
     * @param level
     * @return
     */
    private boolean checkLevel(String level) {
        String s = level.toUpperCase();
        if (s.equals("ALL")) {
            return true;
        } else if (s.equals("DEBUG")) {
            return true;
        } else if (s.equals("INFO")) {
            return true;
        } else if (s.equals("WARN")) {
            return true;
        } else if (s.equals("ERROR")) {
            return true;
        } else if (s.equals("FATAL")) {
            return true;
        } else if (s.equals("OFF")) {
            return true;
        } else if (s.equals("TRACE")) {
            return true;
        } else {
            return false;
        }
    }
}
