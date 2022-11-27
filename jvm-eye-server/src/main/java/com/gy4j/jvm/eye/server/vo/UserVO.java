package com.gy4j.jvm.eye.server.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class UserVO implements Serializable {
    private String id;
    private String username;
    private String sessionId;
}
