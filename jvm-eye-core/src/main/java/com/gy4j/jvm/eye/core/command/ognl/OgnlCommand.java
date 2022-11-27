package com.gy4j.jvm.eye.core.command.ognl;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.ognl.response.OgnlResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import com.gy4j.jvm.eye.core.util.ExpressUtils;
import com.gy4j.jvm.eye.core.util.ObjectUtils;
import lombok.Data;

import java.lang.instrument.Instrumentation;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class OgnlCommand extends AbstractCommand {
    /**
     * ognl表达式
     */
    private String express;
    /**
     * 类加载器hash
     */
    private String classLoaderHash;
    /**
     * 展开层次
     */
    private int expand = 1;
    /**
     * 返回内容阈值
     */
    private int sizeLimit = 5 * 1024 * 1024;
    /**
     * 是否json格式返回
     */
    private boolean showWithJson;

    @Override
    public IResponse executeForResponse(IClient client) {
        Instrumentation inst = client.getInstrumentation();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(inst, classLoaderHash);
        Object value = ExpressUtils.get(express, classLoader, null);
        OgnlResponse ognlResponse = new OgnlResponse();
        ognlResponse.setResult(ObjectUtils.getObjectInfo(value, sizeLimit, expand, isShowWithJson()));
        return ognlResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return OgnlResponse.class;
    }
}