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
@NoArgsConstructor
@AllArgsConstructor
public class GcInfoVO {
    private String name;
    private long collectionCount;
    private long collectionTime;
}
