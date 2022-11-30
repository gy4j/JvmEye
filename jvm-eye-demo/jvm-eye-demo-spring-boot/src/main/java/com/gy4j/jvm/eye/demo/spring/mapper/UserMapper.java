package com.gy4j.jvm.eye.demo.spring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gy4j.jvm.eye.demo.spring.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author gongy
 * 功能：
 * 日期：2022/11/29
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
public interface UserMapper extends BaseMapper<User> {
    User selectUser(@Param("name") String name);
    User selectUserLike(@Param("name") String name);
}