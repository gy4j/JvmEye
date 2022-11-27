package com.gy4j.jvm.eye.core.command.jvm;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.SysEnvResponse;
import com.gy4j.jvm.eye.core.response.IResponse;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SysEnvCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        SysEnvResponse response = new SysEnvResponse();
        response.setInfo(System.getenv());
        return response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SysEnvResponse.class;
    }
}
