package com.gy4j.jvm.eye.demo.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("test/thread")
public class ThreadTestController {
    private static boolean cpuTestFlag = false;

    public ThreadTestController() {
        Thread thread = new Thread(new Runnable() {
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
        });
        thread.setName("ThreadTestController-TestThread");
        thread.start();
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

    @RequestMapping("wait01")
    public String wait01() {
        String msg = "wait01";
        synchronized (this) {
            try {
                // 阻塞600秒
                TimeUnit.SECONDS.sleep(600);
            } catch (InterruptedException e) {
            }
            System.out.println(msg);
        }
        return "wait01";
    }

    @RequestMapping("wait02")
    public String wait02() {
        String msg = "wait02";
        synchronized (this) {
            System.out.println(msg);
        }
        return "wait02";
    }
}
