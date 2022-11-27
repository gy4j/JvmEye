package com.gy4j.jvm.eye.demo.spring.controller;

import com.gy4j.jvm.eye.demo.spring.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/24-17:19
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("test")
public class TestController {
    private static boolean cpuTestFlag = false;

    @Autowired
    private TestService testService;

    private List<String> stringList = new ArrayList<>();

    public TestController() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    doLoopForTestCpu();
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    public void doLoopForTestCpu() {
        while (cpuTestFlag) {
            double a = Math.random();
            double b = Math.random();
            double c = a + b;
        }
    }


    @RequestMapping("startCpuTest")
    public String startCpuTest() {
        cpuTestFlag = true;
        return "startCpuTest";
    }

    @RequestMapping("stopCpuTest")
    public String stopCpuTest() {
        cpuTestFlag = false;
        return "stopCpuTest";
    }


    @RequestMapping("startMemoryTest")
    public String startMemoryTest() {
        stringList.clear();
        for (int i = 0; i < 1000000; i++) {
            stringList.add(new String("testSting" + i));
        }
        return "startMemoryTest";
    }


    @RequestMapping("stopMemoryTest")
    public String stopMemoryTest() {
        stringList.clear();
        return "stopMemoryTest";
    }


    @RequestMapping("testTrace")
    public String testTrace(String msg) {
        for (int i = 0; i < 20; i++) {
            testService.test01();
            testService.test02();
        }
        testService.test03();
        return "testTrace:" + msg;
    }
}
