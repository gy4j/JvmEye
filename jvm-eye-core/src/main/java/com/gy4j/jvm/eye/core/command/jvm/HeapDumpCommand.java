package com.gy4j.jvm.eye.core.command.jvm;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.jvm.response.HeapDumpResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class HeapDumpCommand extends AbstractCommand {
    private boolean live = true;

    @Override
    public IResponse executeForResponse(IClient client) {
        try {
            HeapDumpResponse heapDumpResponse = new HeapDumpResponse();
            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
            File file = File.createTempFile("heapdump" + date + (live ? "-live" : ""), ".hprof");
            String dumpFile = file.getAbsolutePath();
            file.delete();
            run(dumpFile, live);
            heapDumpResponse.setMsg("dump success.");
            heapDumpResponse.setDumpFile(dumpFile);
            return heapDumpResponse;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return HeapDumpResponse.class;
    }

    private void run(String file, boolean live) throws IOException {
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory
                .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        hotSpotDiagnosticMXBean.dumpHeap(file, live);
    }
}
