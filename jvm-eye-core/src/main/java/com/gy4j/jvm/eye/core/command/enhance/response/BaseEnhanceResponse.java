package com.gy4j.jvm.eye.core.command.enhance.response;

import com.gy4j.jvm.eye.core.command.enhance.vo.EnhanceInfoVO;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import lombok.Data;

/**
 * @author gy4j
 */
@Data
public class BaseEnhanceResponse extends BaseResponse {
    protected EnhanceInfoVO enhanceInfo;
}
