package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.core.command.clazz.ClassCommand;
import com.gy4j.jvm.eye.core.command.clazz.JadCommand;
import com.gy4j.jvm.eye.core.command.clazz.MethodCommand;
import com.gy4j.jvm.eye.core.command.clazz.response.ClassResponse;
import com.gy4j.jvm.eye.core.command.clazz.response.JadResponse;
import com.gy4j.jvm.eye.core.command.clazz.response.MethodResponse;
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
 * 日期：2022/11/8-15:35
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("api/class")
public class ApiClassController {
    @Autowired
    private EyeServer eyeServer;

    @RequestMapping("list")
    public ResponseWrapper<ClassResponse> list(@RequestParam String clientId, @RequestBody ClassCommand command) {
        ClassResponse classResponse = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(classResponse);
    }

    @RequestMapping("jad")
    public ResponseWrapper<JadResponse> jad(@RequestParam String clientId
            , @RequestBody JadCommand command) {
        JadResponse jadResponse = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(jadResponse);
    }

    @RequestMapping("method")
    public ResponseWrapper<MethodResponse> method(@RequestParam String clientId, @RequestBody MethodCommand command) {
        MethodResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }
}
