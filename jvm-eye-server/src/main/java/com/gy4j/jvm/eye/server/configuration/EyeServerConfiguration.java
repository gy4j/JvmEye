package com.gy4j.jvm.eye.server.configuration;

import com.gy4j.jvm.eye.server.server.EyeServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/8-9:17
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Configuration
public class EyeServerConfiguration {
    @Bean
    public EyeServer eyeServer() {
        EyeServer eyeServer = new EyeServer(5555);
        eyeServer.start();
        return eyeServer;
    }
}
