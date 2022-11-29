package com.gy4j.jvm.eye.demo.spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author gongy
 * 功能：
 * 日期：2022/11/29
 * 版本       开发者     描述
 * 1.0.0     gongy     ...
 */
@Data
@TableName("Users")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String password;
    private int age;
}
