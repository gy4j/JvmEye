package com.gy4j.jvm.eye.core.command.jvm;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.MemoryResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.JvmUtils;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class MemoryCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        MemoryResponse memoryResponse = new MemoryResponse();
        memoryResponse.setMemoryInfos(JvmUtils.getMemoryInfos());
        return memoryResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return MemoryResponse.class;
    }

}
