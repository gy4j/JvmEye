package com.gy4j.jvm.eye.core.command.clazz;

import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.clazz.response.ClassLoaderResponse;
import com.gy4j.jvm.eye.core.command.clazz.vo.ClassLoaderInfoVO;
import com.gy4j.jvm.eye.core.constant.EyeConstants;
import com.gy4j.jvm.eye.core.response.IResponse;
import lombok.Data;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gy4j
 */
@Data
public class ClassLoaderCommand extends AbstractCommand {

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ClassLoaderResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        // class.getClassLoader()返回是null的是由BootstrapClassLoader加载的，特殊处理
        ClassLoaderInfoVO bootstrapClassLoader = new ClassLoaderInfoVO();
        bootstrapClassLoader.setName("BootstrapClassLoader");
        bootstrapClassLoader.setParentName(EyeConstants.NONE);
        bootstrapClassLoader.setClassLoaderHash(EyeConstants.NONE);

        // 遍历加载的类列表，汇总累加器列表信息
        Instrumentation instrumentation = client.getInstrumentation();
        Map<ClassLoader, ClassLoaderInfoVO> loaderInfos = new HashMap<ClassLoader, ClassLoaderInfoVO>();
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader == null) {
                bootstrapClassLoader.increase();
            } else {
                ClassLoaderInfoVO loaderInfo = checkLoaderInfo(classLoader, loaderInfos);
                ClassLoader parent = classLoader.getParent();
                while (parent != null) {
                    checkLoaderInfo(parent, loaderInfos);
                    parent = parent.getParent();
                }
                loaderInfo.increase();
            }
        }

        // 将BootstrapClassLoader和其他类加载器聚合
        List<ClassLoaderInfoVO> classLoaderInfos = new ArrayList<ClassLoaderInfoVO>();
        classLoaderInfos.add(bootstrapClassLoader);
        classLoaderInfos.addAll(loaderInfos.values());

        ClassLoaderResponse classLoaderResponse = new ClassLoaderResponse();
        classLoaderResponse.setClassLoaderInfos(classLoaderInfos);
        return classLoaderResponse;
    }

    /**
     * 检查类加载器是否在map中存在，不存在则PUT
     *
     * @param classLoader
     * @param loaderInfos
     * @return
     */
    private ClassLoaderInfoVO checkLoaderInfo(ClassLoader classLoader, Map<ClassLoader, ClassLoaderInfoVO> loaderInfos) {
        ClassLoaderInfoVO loaderInfo = loaderInfos.get(classLoader);
        if (loaderInfo == null) {
            loaderInfo = new ClassLoaderInfoVO(classLoader);
            loaderInfos.put(classLoader, loaderInfo);
        }
        return loaderInfo;
    }
}
