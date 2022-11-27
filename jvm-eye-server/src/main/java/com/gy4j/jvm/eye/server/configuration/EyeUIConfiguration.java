package com.gy4j.jvm.eye.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/16-17:51
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Configuration
public class EyeUIConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html").addResourceLocations("classpath:/public/index.html");
        registry.addResourceHandler("/index.html").addResourceLocations("classpath:/public/index.html");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/public/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/public/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/public/js/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/public/fonts/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/public/favicon.ico");
        registry.addResourceHandler("/logo.png").addResourceLocations("classpath:/public/logo.png");
    }
}
