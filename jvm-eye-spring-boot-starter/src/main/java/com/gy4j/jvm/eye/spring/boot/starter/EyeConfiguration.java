package com.gy4j.jvm.eye.spring.boot.starter;

import com.gy4j.jvm.eye.client.EyeClient;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/7-11:37
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Configuration
@ConditionalOnProperty(name = "jvm.eye.enable", havingValue = "true")
public class EyeConfiguration {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${jvm.eye.host:localhost}")
    private String eyeHost;

    @Value("${jvm.eye.port:5555}")
    private int eyePort;

    @Bean
    public EyeClient eyeClient() {
        EyeClient eyeClient = new EyeClient(ByteBuddyAgent.install()
                , appName, eyeHost, eyePort);
        return eyeClient;
    }
}
