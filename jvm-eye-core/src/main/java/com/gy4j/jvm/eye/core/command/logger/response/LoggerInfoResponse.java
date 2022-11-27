package com.gy4j.jvm.eye.core.command.logger.response;

import com.gy4j.jvm.eye.core.command.logger.vo.LoggerInfoVO;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author gy4j
 */
@Data
public class LoggerInfoResponse extends BaseResponse {
    private List<LoggerInfoVO> loggerInfos;
}
