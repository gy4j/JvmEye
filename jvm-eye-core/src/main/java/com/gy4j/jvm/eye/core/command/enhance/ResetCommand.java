package com.gy4j.jvm.eye.core.command.enhance;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.enhance.response.ResetResponse;
import com.gy4j.jvm.eye.core.command.enhance.vo.EnhanceInfoVO;
import com.gy4j.jvm.eye.core.enhance.EnhanceManager;
import com.gy4j.jvm.eye.core.enhance.EnhancerAffect;
import com.gy4j.jvm.eye.core.response.IResponse;
import lombok.Data;

/**
 * @author gy4j
 */
@Data
public class ResetCommand extends AbstractCommand {
    /**
     * 需要清理的命令ID，为空时清理session所有的commandId
     */
    private String resetCommandId;

    @Override
    public IResponse executeForResponse(IClient client) {
        EnhancerAffect enhancerAffect = EnhanceManager.reset(client.getInstrumentation()
                , resetCommandId, getSessionId());
        ResetResponse response = new ResetResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ResetResponse.class;
    }
}
