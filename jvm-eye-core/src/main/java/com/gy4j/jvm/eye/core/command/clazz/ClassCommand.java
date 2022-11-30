package com.gy4j.jvm.eye.core.command.clazz;


import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.clazz.response.ClassResponse;
import com.gy4j.jvm.eye.core.command.clazz.vo.ClassInfoVO;
import com.gy4j.jvm.eye.core.response.IResponse;
import com.gy4j.jvm.eye.core.util.ClassLoaderUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author gy4j
 */
@Data
public class ClassCommand extends AbstractCommand {
    /**
     * 类名（模糊查找）
     */
    private String className;
    /**
     * 限制返回数
     */
    private int numberOfLimit = 100;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ClassResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        Set<Class<?>> classSet = ClassLoaderUtils.findClasses(client.getInstrumentation()
                , className);

        List<ClassInfoVO> classInfos = new ArrayList<ClassInfoVO>();
        for (Class<?> clazz : classSet) {
            classInfos.add(new ClassInfoVO(clazz));
            if (classInfos.size() >= numberOfLimit) {
                break;
            }
        }

        ClassResponse classResponse = new ClassResponse();
        classResponse.setClassInfos(classInfos);
        return classResponse;
    }
}
