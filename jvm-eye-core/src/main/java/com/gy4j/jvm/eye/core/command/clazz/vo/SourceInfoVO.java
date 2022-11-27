package com.gy4j.jvm.eye.core.command.clazz.vo;

import lombok.Data;

/**
 * @author gy4j
 */
@Data
public class SourceInfoVO {
    /**
     * 源码
     */
    private String source;

    /**
     * class信息
     */
    private ClassInfoVO classInfo;
}
