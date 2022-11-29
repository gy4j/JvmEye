package com.gy4j.jvm.eye.server.aop;

import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.server.vo.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/8-15:35
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Aspect
@Slf4j
@Configuration
public class ApiAspect {
    @Pointcut("execution(* com.gy4j.jvm.eye.server.controller.Api*Controller.*(..))")
    public void execute() {

    }

    @Around("execute()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        try {
//            if (SessionHelper.getSessionUserId() == null) {
//                return ResponseWrapper.notAuth();
//            }
            log.info("请求：" + JsonUtils.toJson(pjp.getArgs()));
            Object result = pjp.proceed();
            if (result instanceof ResponseWrapper) {
                return result;
            } else {
                return ResponseWrapper.ok(result);
            }
        } catch (Exception ex) {
            log.error("接口调用异常：方法:" + pjp.getSignature() + ",参数:", ex);
            if (ex instanceof IllegalArgumentException) {
                return ResponseWrapper.fail(ex.getMessage());
            } else {
                return ResponseWrapper.fail("接口调用异常!");
            }
        }
    }
}

