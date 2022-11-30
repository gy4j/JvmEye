package com.gy4j.jvm.eye.core.command.clazz;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.clazz.response.MethodResponse;
import com.gy4j.jvm.eye.core.command.clazz.vo.MethodInfoVO;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author gy4j
 */
@Data
public class MethodCommand extends AbstractCommand {
    /**
     * 类名（模糊查找）
     */
    private String className;
    /**
     * 方法名（模糊查找）
     */
    private String methodName;
    /**
     * 限制返回数
     */
    private int numberOfLimit = 100;

    @Override
    public IResponse executeForResponse(IClient client) {
        Set<Class<?>> classSet = ClassLoaderUtils.findClasses(client.getInstrumentation()
                , className);
        List<MethodInfoVO> methods = new ArrayList<MethodInfoVO>();
        for (Class<?> clazz : classSet) {
            try {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().indexOf(methodName) != -1) {
                        MethodInfoVO methodInfo = new MethodInfoVO(method, clazz);
                        methods.add(methodInfo);
                    }
                    if (methods.size() >= numberOfLimit) {
                        break;
                    }
                }
            } catch (Throwable throwable) {
                // ignore
            }
        }

        MethodResponse methodResponse = new MethodResponse();
        methodResponse.setMethodInfos(methods);
        return methodResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return MethodResponse.class;
    }
}
