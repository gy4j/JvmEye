package com.gy4j.jvm.eye.demo.spring.controller;

import com.gy4j.jvm.eye.demo.spring.service.MethodTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author gongy
 * 功能：
 * 日期：2022/12/1
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@RestController
@RequestMapping("test/method")
public class MethodTestController {
    @Autowired
    private MethodTestService methodTestService;

    @RequestMapping("testStack")
    public void testStack() {
        methodTestService.call01();
    }

    @RequestMapping("testTrace")
    public void testTrace() {
        for (int i = 0; i < 5; i++) {
            this.sleep100ms();
        }
        this.sleep200ms();
        this.sleep200ms();
        this.sleep200ms();
        this.sleep450ms();
    }

    @RequestMapping("testWatch")
    public String testWatch(@RequestParam("type") String type,
                            @RequestParam("msg") String msg) {
        if ("return".equals(type)) {
            return "test return:" + msg;
        } else if ("throw".equals(type)) {
            throw new RuntimeException("test throw:" + msg);
        }
        return "unknown type" + type;
    }

    @RequestMapping("testCall")
    public String testCall(String msg) {
        System.out.println(msg);
        msg = msg + "_modified";
        return "modified msg:" + msg;
    }

    private void sleep450ms() {
        try {
            TimeUnit.MILLISECONDS.sleep(450);
        } catch (InterruptedException e) {
        }
    }

    private void sleep200ms() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    private void sleep100ms() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
        }
    }
}
