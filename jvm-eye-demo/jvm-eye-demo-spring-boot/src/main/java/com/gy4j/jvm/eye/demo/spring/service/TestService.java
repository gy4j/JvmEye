package com.gy4j.jvm.eye.demo.spring.service;

import org.springframework.stereotype.Service;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/24-18:03
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Service
public class TestService {
    public void test01() {
        System.out.println(Math.random() * 10);
    }

    public void test02() {
        System.out.println(Math.random() * 100);
    }

    public String test03() {
        System.out.println(Math.random() * 100);
        return "test03------" + Math.random() * 100;
    }
}
