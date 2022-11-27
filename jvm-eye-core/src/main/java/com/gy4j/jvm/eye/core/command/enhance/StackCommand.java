package com.gy4j.jvm.eye.core.command.enhance;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.EnhanceCommand;
import com.gy4j.jvm.eye.core.command.enhance.response.StackResponse;
import com.gy4j.jvm.eye.core.command.enhance.vo.EnhanceInfoVO;
import com.gy4j.jvm.eye.core.enhance.EnhancerAffect;
import com.gy4j.jvm.eye.core.listener.AbstractEnhanceAdviceListener;
import com.gy4j.jvm.eye.core.listener.StackEnhanceListener;
import com.gy4j.jvm.eye.core.response.IResponse;
import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class StackCommand extends EnhanceCommand {
    /**
     * 是否加强jdk方法
     */
    protected boolean skipJDKTrace = false;

    @Override
    public IResponse getNormalResponse(EnhancerAffect enhancerAffect) {
        StackResponse response = new StackResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }

    @Override
    public AbstractEnhanceAdviceListener getAdviceListener(IClient client) {
        return new StackEnhanceListener(client, this);
    }

    @Override
    public boolean isTracing() {
        return false;
    }

    @Override
    public boolean isSkipJDKTrace() {
        return skipJDKTrace;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return StackResponse.class;
    }
}
