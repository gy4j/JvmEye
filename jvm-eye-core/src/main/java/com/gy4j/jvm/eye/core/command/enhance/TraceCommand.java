package com.gy4j.jvm.eye.core.command.enhance;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.EnhanceCommand;
import com.gy4j.jvm.eye.core.command.enhance.response.TraceResponse;
import com.gy4j.jvm.eye.core.command.enhance.vo.EnhanceInfoVO;
import com.gy4j.jvm.eye.core.enhance.EnhancerAffect;
import com.gy4j.jvm.eye.core.listener.AbstractEnhanceAdviceListener;
import com.gy4j.jvm.eye.core.listener.TraceEnhanceListener;
import com.gy4j.jvm.eye.core.response.IResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class TraceCommand extends EnhanceCommand {
    private static final Logger logger = LoggerFactory.getLogger(TraceCommand.class);

    /**
     * 是否加强jdk方法
     */
    protected boolean skipJDKTrace = false;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return TraceResponse.class;
    }

    @Override
    public IResponse getNormalResponse(EnhancerAffect enhancerAffect) {
        TraceResponse response = new TraceResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }

    @Override
    public AbstractEnhanceAdviceListener getAdviceListener(IClient client) {
        return new TraceEnhanceListener(client, this);
    }

    @Override
    public boolean isTracing() {
        return true;
    }
}
