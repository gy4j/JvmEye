package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.core.command.enhance.ResetCommand;
import com.gy4j.jvm.eye.core.command.enhance.StackCommand;
import com.gy4j.jvm.eye.core.command.enhance.TraceCommand;
import com.gy4j.jvm.eye.core.command.enhance.WatchCommand;
import com.gy4j.jvm.eye.core.command.enhance.response.ResetResponse;
import com.gy4j.jvm.eye.core.command.enhance.response.StackResponse;
import com.gy4j.jvm.eye.core.command.enhance.response.TraceResponse;
import com.gy4j.jvm.eye.core.command.enhance.response.WatchResponse;
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
 * 日期：2022/11/13-13:03
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("api/enhance")
public class ApiEnhanceController {
    @Autowired
    private EyeServer eyeServer;

    @RequestMapping("trace")
    public ResponseWrapper<TraceResponse> trace(@RequestParam String clientId
            , @RequestBody TraceCommand command) {
        TraceResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("watch")
    public ResponseWrapper<WatchResponse> watch(@RequestParam String clientId
            , @RequestBody WatchCommand command) {
        WatchResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("stack")
    public ResponseWrapper<StackResponse> stack(@RequestParam String clientId, @RequestBody StackCommand command) {
        StackResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("reset")
    public ResponseWrapper<ResetResponse> reset(@RequestParam String clientId, @RequestBody ResetCommand command) {
        ResetResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }
}
