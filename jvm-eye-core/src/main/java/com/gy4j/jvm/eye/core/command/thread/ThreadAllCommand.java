package com.gy4j.jvm.eye.core.command.thread;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.thread.response.ThreadAllResponse;
import com.gy4j.jvm.eye.core.command.thread.vo.ThreadSampleInfoVO;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ThreadUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class ThreadAllCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(ThreadAllCommand.class);
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    /**
     * 采样间隔，毫秒
     */
    private int sampleInterval = 200;
    /**
     * if true, retrieves all locked monitors
     */
    private boolean lockedMonitors = false;
    /**
     * if true, retrieves all locked ownable synchronizers.
     */
    private boolean lockedSynchronizers = false;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ThreadAllResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        ThreadAllResponse threadResponse = new ThreadAllResponse();
        List<ThreadSampleInfoVO> threadInfoList = ThreadUtils.findAllThreadInfos(sampleInterval, lockedMonitors, lockedSynchronizers);
        threadResponse.setThreadSampleInfos(threadInfoList);
        return threadResponse;
    }
}
