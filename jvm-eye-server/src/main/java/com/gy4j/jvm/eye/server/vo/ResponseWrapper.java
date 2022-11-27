package com.gy4j.jvm.eye.server.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
@Builder
public class ResponseWrapper<T> implements Serializable {
    private static final int CODE_SUCCESS = 200;
    private static final int CODE_NOT_AUTH = 401;
    private static final int CODE_FAIL = 500;

    private String msg;
    private int code;
    private T data;

    public static <T> ResponseWrapper<T> ok(T data) {
        return (ResponseWrapper<T>) ResponseWrapper.builder().code(CODE_SUCCESS).data(data).build();
    }

    public static <T> ResponseWrapper<T> fail(String msg) {
        return (ResponseWrapper<T>) ResponseWrapper.builder().code(CODE_FAIL).msg(msg).build();
    }

    public static <T> ResponseWrapper<T> notAuth() {
        return (ResponseWrapper<T>) ResponseWrapper.builder().code(CODE_NOT_AUTH).build();
    }
}