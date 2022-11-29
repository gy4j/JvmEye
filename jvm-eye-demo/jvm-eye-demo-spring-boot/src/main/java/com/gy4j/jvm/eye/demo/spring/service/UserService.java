package com.gy4j.jvm.eye.demo.spring.service;

import com.gy4j.jvm.eye.demo.spring.entity.User;
import com.gy4j.jvm.eye.demo.spring.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gongy
 * 功能：
 * 日期：2022/11/29
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> getUsers() {
        return userMapper.selectList(null);
    }


    public User getUser(String name) {
        return userMapper.selectUserLike(name);
    }
}
