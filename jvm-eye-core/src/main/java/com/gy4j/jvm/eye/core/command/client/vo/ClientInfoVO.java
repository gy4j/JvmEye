package com.gy4j.jvm.eye.core.command.client.vo;

import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class ClientInfoVO {
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 客户端服务器名称
     */
    private String host;
    /**
     * 客户端服务器IP
     */
    private String ip;
    /**
     * 版本
     */
    private String version;
}
