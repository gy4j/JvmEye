package com.gy4j.jvm.eye.core.command.enhance.vo;

import com.gy4j.jvm.eye.core.enhance.EnhancerAffect;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class EnhanceInfoVO {
    private long cost;
    private int classCnt;
    private int methodCnt;
    private String stackErrorTrace;
    private List<String> classDumpPaths;
    private List<String> methods;

    public EnhanceInfoVO() {

    }

    public EnhanceInfoVO(EnhancerAffect enhancerAffect) {
        this.cost = enhancerAffect.cost();
        this.classCnt = enhancerAffect.classCnt();
        this.methodCnt = enhancerAffect.methodCnt();
        if (enhancerAffect.getThrowable() != null) {
            this.stackErrorTrace = enhancerAffect.getThrowable().toString();
        }
        this.classDumpPaths = new ArrayList<String>();
        if (enhancerAffect.getClassDumpFiles() != null) {
            for (File f : enhancerAffect.getClassDumpFiles()) {
                this.classDumpPaths.add(f.getAbsolutePath());
            }
        }
        this.methods = enhancerAffect.getMethods();
    }
}
