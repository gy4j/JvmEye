package com.gy4j.jvm.eye.core.command.clazz.response;

import com.gy4j.jvm.eye.core.command.clazz.vo.MethodInfoVO;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class MethodResponse extends BaseResponse {
    private List<MethodInfoVO> methodInfos;
}
