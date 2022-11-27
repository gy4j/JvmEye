package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.core.command.clazz.ClassLoaderCommand;
import com.gy4j.jvm.eye.core.command.clazz.response.ClassLoaderResponse;
import com.gy4j.jvm.eye.core.command.jvm.HeapDumpCommand;
import com.gy4j.jvm.eye.core.command.jvm.JvmCommand;
import com.gy4j.jvm.eye.core.command.jvm.MemoryCommand;
import com.gy4j.jvm.eye.core.command.jvm.SysEnvCommand;
import com.gy4j.jvm.eye.core.command.jvm.SysPropCommand;
import com.gy4j.jvm.eye.core.command.jvm.VmOptionCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.HeapDumpResponse;
import com.gy4j.jvm.eye.core.command.jvm.response.JvmResponse;
import com.gy4j.jvm.eye.core.command.jvm.response.MemoryResponse;
import com.gy4j.jvm.eye.core.command.jvm.response.SysEnvResponse;
import com.gy4j.jvm.eye.core.command.jvm.response.SysPropResponse;
import com.gy4j.jvm.eye.core.command.jvm.response.VmOptionResponse;
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
 * 日期：2022/11/13-11:17
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("api/jvm")
public class ApiJvmController {
    @Autowired
    private EyeServer eyeServer;

    @RequestMapping("classloader")
    public ResponseWrapper<ClassLoaderResponse> classloader(@RequestParam String clientId) {
        ClassLoaderCommand command = new ClassLoaderCommand();
        ClassLoaderResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("info")
    public ResponseWrapper<JvmResponse> info(@RequestParam String clientId) {
        JvmCommand command = new JvmCommand();
        JvmResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("memory")
    public ResponseWrapper<MemoryResponse> memory(@RequestParam String clientId) {
        MemoryCommand command = new MemoryCommand();
        MemoryResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }


    @RequestMapping("sysprop")
    public ResponseWrapper<SysPropResponse> sysprop(@RequestParam String clientId) {
        SysPropCommand command = new SysPropCommand();
        SysPropResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("sysenv")
    public ResponseWrapper<SysEnvResponse> sysenv(@RequestParam String clientId) {
        SysEnvCommand command = new SysEnvCommand();
        SysEnvResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("vmoption")
    public ResponseWrapper<VmOptionResponse> vmoption(@RequestParam String clientId) {
        VmOptionCommand command = new VmOptionCommand();
        VmOptionResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }

    @RequestMapping("heapdump")
    public ResponseWrapper<HeapDumpResponse> heapdump(@RequestParam String clientId, @RequestBody HeapDumpCommand command) {
        HeapDumpResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response);
    }
}
