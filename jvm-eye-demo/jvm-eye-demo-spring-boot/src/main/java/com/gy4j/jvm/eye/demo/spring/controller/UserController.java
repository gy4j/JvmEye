package com.gy4j.jvm.eye.demo.spring.controller;

import com.gy4j.jvm.eye.demo.spring.entity.User;
import com.gy4j.jvm.eye.demo.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gongy
 * 功能：
 * 日期：2022/11/29
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    private String name = "hello";

    @RequestMapping("list")
    public List<User> list() {
        return userService.getUsers();
    }

    @RequestMapping("info")
    public User info(String name) {
        User user = userService.getUser(name);
        user.setName(this.name);
        return user;
    }
}
