package com.gy4j.jvm.eye.demo.spring.service;

import org.springframework.stereotype.Service;

/**
 * @author gongy
 * 功能：
 * 日期：2022/12/1
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@Service
public class MethodTestService {
    public void call01() {
        this.call02();
    }

    public void call02() {
        this.call03();
    }

    public void call03() {
        System.out.println("call01-->call02-->call03");
    }
}
