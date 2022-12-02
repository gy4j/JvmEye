package com.gy4j.jvm.eye.demo.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gongy
 * 功能：
 * 日期：2022/12/1
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@RestController
@RequestMapping("test/logger")
public class LoggerTestController {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTestController.class);

    @RequestMapping("testLogger")
    public void testLogger() {
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
    }
}
