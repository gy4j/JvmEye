package com.gy4j.jvm.eye.demo.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongy
 * 功能：
 * 日期：2022/12/1
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@RestController
@RequestMapping("test/memory")
public class MemoryTestController {
    private List<String> stringList = new ArrayList<>();

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
}
