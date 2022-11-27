package com.gy4j.jvm.eye.core.command.jvm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryInfoVO {
    public static final String TYPE_HEAP = "heap";
    public static final String TYPE_NON_HEAP = "non_heap";
    public static final String TYPE_BUFFER_POOL = "buffer_pool";

    private String type;
    private String name;
    private long used;
    private long total;
    private long max;
}