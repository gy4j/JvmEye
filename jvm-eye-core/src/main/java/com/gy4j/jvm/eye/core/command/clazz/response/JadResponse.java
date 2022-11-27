package com.gy4j.jvm.eye.core.command.clazz.response;

import com.gy4j.jvm.eye.core.command.clazz.vo.SourceInfoVO;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import lombok.Data;

/**
 * @author gy4j
 */
@Data
public class JadResponse extends BaseResponse {
    /**
     * 类源码信息
     */
    private SourceInfoVO sourceInfo;
}
