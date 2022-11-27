package com.gy4j.jvm.eye.core.command.jvm;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.VmOptionResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class VmOptionCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory
                .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        List<VMOption> vmOptions = hotSpotDiagnosticMXBean.getDiagnosticOptions();
        VmOptionResponse vmOptionResponse = new VmOptionResponse();
        vmOptionResponse.setVmOptions(vmOptions);
        return vmOptionResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return VmOptionResponse.class;
    }
}
