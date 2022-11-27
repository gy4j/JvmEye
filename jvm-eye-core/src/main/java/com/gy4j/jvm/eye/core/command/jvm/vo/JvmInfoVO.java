package com.gy4j.jvm.eye.core.command.jvm.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class JvmInfoVO {
    private Map<String, String> fileDescriptorInfo = new LinkedHashMap<String, String>();
    private Map<String, String> runtimeInfo = new LinkedHashMap<String, String>();
    private Map<String, String> classLoadingInfo = new LinkedHashMap<String, String>();
    private Map<String, String> compilationInfo = new LinkedHashMap<String, String>();
    private Map<String, String> garbageCollectorInfo = new LinkedHashMap<String, String>();
    private Map<String, String> memoryManagerInfo = new LinkedHashMap<String, String>();
    private Map<String, String> memoryInfo = new LinkedHashMap<String, String>();
    private Map<String, String> operatingSystemInfo = new LinkedHashMap<String, String>();
    private Map<String, String> threadInfo = new LinkedHashMap<String, String>();
}
