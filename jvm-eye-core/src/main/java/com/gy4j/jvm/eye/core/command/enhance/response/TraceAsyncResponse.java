package com.gy4j.jvm.eye.core.command.enhance.response;

import com.gy4j.jvm.eye.core.command.enhance.vo.TraceInfoVO;
import com.gy4j.jvm.eye.core.response.AbstractAsyncResponse;
import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class TraceAsyncResponse extends AbstractAsyncResponse {
    private TraceInfoVO traceInfo;
}
