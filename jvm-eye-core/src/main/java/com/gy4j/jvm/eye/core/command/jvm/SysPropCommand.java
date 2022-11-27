package com.gy4j.jvm.eye.core.command.jvm;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.SysPropResponse;
import com.gy4j.jvm.eye.core.response.IResponse;

import java.util.Properties;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SysPropCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        SysPropResponse response = new SysPropResponse();
        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            response.getInfo().put(key, properties.getProperty(key));
        }
        return response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SysPropResponse.class;
    }
}
