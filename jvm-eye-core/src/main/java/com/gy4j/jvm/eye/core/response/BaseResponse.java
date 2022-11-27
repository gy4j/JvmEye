package com.gy4j.jvm.eye.core.response;

import com.gy4j.jvm.eye.core.util.JsonUtils;
import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class BaseResponse implements IResponse {
    public static final int STATUS_OK = 0;
    public static final int STATUS_FAIL = -1;

    /**
     * 命令ID
     */
    protected String commandId;
    /**
     * 客户端ID
     */
    protected String clientId;
    /**
     * 响应状态
     */
    protected int status;
    /**
     * 响应信息
     */
    protected String msg;

    /**
     * 通用失败返回
     *
     * @param msg
     * @param responseClass
     * @return
     */
    public static IResponse fail(String msg, Class<? extends IResponse> responseClass) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMsg(msg);
        baseResponse.setStatus(STATUS_FAIL);
        return JsonUtils.parseJson(JsonUtils.toJson(baseResponse), responseClass);
    }
}