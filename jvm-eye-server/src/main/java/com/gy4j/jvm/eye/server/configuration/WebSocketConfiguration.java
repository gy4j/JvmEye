package com.gy4j.jvm.eye.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/15-20:13
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@EnableWebSocket
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }
}