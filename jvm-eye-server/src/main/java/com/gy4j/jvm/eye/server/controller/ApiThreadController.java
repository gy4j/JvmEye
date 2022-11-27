package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.core.command.thread.ThreadAllCommand;
import com.gy4j.jvm.eye.core.command.thread.response.ThreadAllResponse;
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
@RequestMapping("api/thread")
public class ApiThreadController {
    @Autowired
    private EyeServer eyeServer;

    @RequestMapping("all")
    public ResponseWrapper<ThreadAllResponse> all(@RequestParam String clientId, @RequestBody ThreadAllCommand command) {
        ThreadAllResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }
}
