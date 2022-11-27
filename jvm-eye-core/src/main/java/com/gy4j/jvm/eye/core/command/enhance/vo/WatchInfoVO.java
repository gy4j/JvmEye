package com.gy4j.jvm.eye.core.command.enhance.vo;

import com.gy4j.jvm.eye.core.command.enhance.WatchCommand;
import com.gy4j.jvm.eye.core.constant.EyeConstants;
import com.gy4j.jvm.eye.core.enhance.EnhanceAdvice;
import com.gy4j.jvm.eye.core.util.ObjectUtils;
import lombok.Data;

import java.util.Date;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/11-14:42
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class WatchInfoVO {
    private Date ts;
    private double cost;
    private Integer expand;
    private Integer sizeLimit;
    private String className;
    private String methodName;
    private String accessPoint;

    private String targetInfo;
    private String paramsInfo;
    private String returnObjInfo;
    private String exceptionInfo;

    public WatchInfoVO() {

    }

    public WatchInfoVO(EnhanceAdvice advice, WatchCommand watchCommand, double cost) {
        setTs(new Date());
        setCost(cost);
        setExpand(watchCommand.getExpand());
        setSizeLimit(watchCommand.getSizeLimit());
        setClassName(advice.getClazz().getName());
        setMethodName(advice.getMethodName());
        if (advice.isAtBefore()) {
            setAccessPoint(EyeConstants.ACCESS_POINT_BEFORE);
        } else if (advice.isAtAfter()) {
            setAccessPoint(EyeConstants.ACCESS_POINT_AFTER);
        } else if (advice.isAtException()) {
            setAccessPoint(EyeConstants.ACCESS_POINT_EXCEPTION);
        }
        if (watchCommand.isShowTarget()) {
            setTargetInfo(ObjectUtils.getObjectInfo(advice.getTarget()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand()));
        }
        if (watchCommand.isShowParams()) {
            setParamsInfo(ObjectUtils.getObjectInfo(advice.getParams()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand(), watchCommand.isShowWithJson()));
        }
        if (watchCommand.isShowReturnObj()) {
            setReturnObjInfo(ObjectUtils.getObjectInfo(advice.getReturnObj()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand(), watchCommand.isShowWithJson()));
        }
        if (watchCommand.isShowException()) {
            setExceptionInfo(ObjectUtils.getObjectInfo(advice.getException()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand()));
        }
    }
}
