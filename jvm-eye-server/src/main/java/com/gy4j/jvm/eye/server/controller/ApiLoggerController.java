package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.core.command.logger.LoggerInfoCommand;
import com.gy4j.jvm.eye.core.command.logger.LoggerLevelCommand;
import com.gy4j.jvm.eye.core.command.logger.response.LoggerInfoResponse;
import com.gy4j.jvm.eye.core.command.logger.response.LoggerLevelResponse;
import com.gy4j.jvm.eye.server.helper.CommandHelper;
import com.gy4j.jvm.eye.server.server.EyeServer;
import com.gy4j.jvm.eye.server.vo.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/13-1:19
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("api/logger")
public class ApiLoggerController {
    @Autowired
    private EyeServer eyeServer;

    @RequestMapping("info")
    public ResponseWrapper<LoggerInfoResponse> detail(@RequestParam String clientId, @RequestBody LoggerInfoCommand command) {
        LoggerInfoResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("level")
    public ResponseWrapper<LoggerLevelResponse> detail(@RequestParam String clientId, @RequestBody LoggerLevelCommand command) {
        LoggerLevelResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }
}
