package com.gy4j.jvm.eye.core.command.vmtool;

import arthas.VmTool;
import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.vmtool.response.VmToolResponse;
import com.gy4j.jvm.eye.core.response.BaseResponse;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import com.gy4j.jvm.eye.core.util.ExpressUtils;
import com.gy4j.jvm.eye.core.util.JsonUtils;
import com.gy4j.jvm.eye.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class VmToolCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(VmToolCommand.class);

    /**
     * 类名(精确匹配)
     */
    protected String className;
    /**
     * 类加载器hashcode(可选，精确匹配)
     */
    protected String classLoaderHash;
    /**
     * 类加载器名称(可选，精确匹配)
     */
    protected String classLoaderName;
    /**
     * 表达式(可选，格式：xxx)
     */
    protected String express;

    @Override
    public IResponse executeForResponse(IClient client) {
        if (StringUtils.isBlank(classLoaderHash) && !StringUtils.isBlank(classLoaderName)) {
            ClassLoader classLoader = ClassLoaderUtils.getClassLoaderByName(client.getInstrumentation(), classLoaderName);
            classLoaderHash = ClassLoaderUtils.getClassLoaderHash(classLoader);
        }

        List<Class<?>> classList = ClassLoaderUtils.findClassesOnly(client.getInstrumentation(), className, classLoaderHash);


        if (classList.size() == 0) {
            return BaseResponse.fail("找不到类：" + className, VmToolResponse.class);
        }
        if (classList.size() > 1) {
            return BaseResponse.fail("找到的类数>1：" + className, VmToolResponse.class);
        }
        Class<?> clazz = classList.get(0);
        VmTool vmTool = VmTool.getInstance();
        Object[] instances = vmTool.getInstances(clazz, 10);
        if (instances.length == 0) {
            return BaseResponse.fail("找不到类实例：" + className, VmToolResponse.class);
        }
        VmToolResponse vmToolResponse = new VmToolResponse();
        Object object = ExpressUtils.get(express, clazz.getClassLoader(), new InstancesWrapper(instances));
        vmToolResponse.setReturnObj(JsonUtils.toJson(object));
        return vmToolResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return VmToolResponse.class;
    }

    static class InstancesWrapper {
        Object instances;

        public InstancesWrapper(Object instances) {
            this.instances = instances;
        }

        public Object getInstances() {
            return instances;
        }

        public void setInstances(Object instances) {
            this.instances = instances;
        }
    }
}
