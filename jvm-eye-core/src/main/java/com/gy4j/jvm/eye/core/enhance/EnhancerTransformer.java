package com.gy4j.jvm.eye.core.enhance;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.asm.location.Location;
import com.alibaba.bytekit.asm.location.LocationType;
import com.alibaba.bytekit.asm.location.MethodInsnNodeWare;
import com.alibaba.bytekit.asm.location.filter.GroupLocationFilter;
import com.alibaba.bytekit.asm.location.filter.InvokeCheckLocationFilter;
import com.alibaba.bytekit.asm.location.filter.InvokeContainLocationFilter;
import com.alibaba.bytekit.asm.location.filter.LocationFilter;
import com.alibaba.bytekit.utils.AsmOpUtils;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.AbstractInsnNode;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodInsnNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.gy4j.jvm.eye.core.listener.AbstractEnhanceAdviceListener;
import com.gy4j.jvm.eye.core.listener.AdviceListenerManager;
import com.gy4j.jvm.eye.core.listener.InvokeListener;
import com.gy4j.jvm.eye.core.spy.SpyAPI;
import com.gy4j.jvm.eye.core.spy.SpyImpl;
import com.gy4j.jvm.eye.core.spy.SpyInterceptors;
import com.gy4j.jvm.eye.core.util.ClassUtils;
import com.gy4j.jvm.eye.core.util.FileUtils;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.lang.System.arraycopy;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EnhancerTransformer implements ClassFileTransformer {
    private static final Logger logger = LoggerFactory.getLogger(EnhancerTransformer.class);

    /**
     * 需要增强或者转换的类集合
     */
    private Set<Class<?>> classesToEnhance;
    /**
     * 需要增强或者转换的方法
     */
    private String methodName;
    /**
     * 切面侦听
     */
    private final AbstractEnhanceAdviceListener listener;
    /**
     * 增强影响范围
     */
    private final EnhancerAffect affect;
    /**
     * 是否trace
     */
    private boolean tracing;
    /**
     * 是否跳过JDK方法
     */
    private boolean skipJDKTrace;


    private static SpyImpl spyImpl = new SpyImpl();

    static {
        SpyAPI.setSpy(spyImpl);
    }

    public EnhancerTransformer(Set<Class<?>> classSet, String methodName, AbstractEnhanceAdviceListener listener
            , boolean isTracing, boolean isSkipJDKTrace) {
        this.classesToEnhance = classSet;
        this.methodName = methodName;
        this.listener = listener;
        this.tracing = isTracing;
        this.skipJDKTrace = isSkipJDKTrace;
        listener.setEnhancerTransformer(this);
        this.affect = new EnhancerAffect();
    }

    public EnhancerAffect enhance(Instrumentation instrumentation) {
        // 过滤掉无法被增强的类
        List<Pair<Class<?>, String>> filteredList = filter(classesToEnhance);
        if (!filteredList.isEmpty()) {
            for (Pair<Class<?>, String> filterPair : filteredList) {
                logger.debug("ignore class: {}, reason: {}", filterPair.getFirst().getName(), filterPair.getSecond());
            }
        }

        this.affect.setTransformer(this);

        try {
            EnhanceManager.addTransformer(this, tracing);

            final int size = classesToEnhance.size();
            final Class<?>[] classArray = new Class<?>[size];
            arraycopy(classesToEnhance.toArray(), 0, classArray, 0, size);
            if (classArray.length > 0) {
                instrumentation.retransformClasses(classArray);
            }
        } catch (Throwable e) {
            affect.setThrowable(e);
        }

        return affect;
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if (classesToEnhance != null && !classesToEnhance.contains(classBeingRedefined)) {
                return null;
            } else {//keep origin class reader for bytecode optimizations, avoiding JVM metaspace OOM.
                ClassNode classNode = new ClassNode(Opcodes.ASM9);
                ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
                // remove JSR https://github.com/alibaba/arthas/issues/1304
                classNode = AsmUtils.removeJSRInstructions(classNode);

                // 获取匹配的方法列表
                List<MethodNode> matchedMethods = new ArrayList<MethodNode>();
                for (MethodNode methodNode : classNode.methods) {
                    if (!isIgnore(methodNode, methodName)) {
                        matchedMethods.add(methodNode);
                    }
                }

                // https://github.com/alibaba/arthas/issues/1690
                if (AsmUtils.isEnhancerByCGLIB(className)) {
                    for (MethodNode methodNode : matchedMethods) {
                        if (AsmUtils.isConstructor(methodNode)) {
                            AsmUtils.fixConstructorExceptionTable(methodNode);
                        }
                    }
                }

                final List<InterceptorProcessor> interceptorProcessors = generateInterceptorProcessor();
                final GroupLocationFilter groupLocationFilter = generateGroupLocationFilter();

                for (MethodNode methodNode : matchedMethods) {
                    if (AsmUtils.isNative(methodNode)) {
                        String methodDeclaration = AsmUtils.methodDeclaration(Type.getObjectType(classNode.name), methodNode);
                        continue;
                    }
                    // 先查找是否有 atBeforeInvoke 函数，如果有，则说明已经有trace了，则直接不再尝试增强，直接插入 listener
                    if (AsmUtils.containsMethodInsnNode(methodNode, Type.getInternalName(SpyAPI.class), "atBeforeInvoke")) {
                        for (AbstractInsnNode insnNode = methodNode.instructions.getFirst();
                             insnNode != null; insnNode = insnNode.getNext()) {
                            if (insnNode instanceof MethodInsnNode) {
                                final MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                                if (listener instanceof InvokeListener && !isIgnoreListener(methodInsnNode)) {
                                    AdviceListenerManager.registerInvokeListener(loader, className,
                                            methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, listener);
                                }
                            }
                        }
                    } else {
                        MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode, groupLocationFilter);
                        for (InterceptorProcessor interceptor : interceptorProcessors) {
                            enhanceMethod(interceptor, methodProcessor, loader, classNode, methodNode, className);
                        }
                    }
                    // enter/exist 总是要插入 listener
                    AdviceListenerManager.registerAdviceListener(loader, className, methodNode.name, methodNode.desc,
                            listener);
                    affect.addMethodAndCount(loader, className, methodNode.name, methodNode.desc);
                }

                // https://github.com/alibaba/arthas/issues/1223 , V1_5 的major version是49
                if (AsmUtils.getMajorVersion(classNode.version) < 49) {
                    classNode.version = AsmUtils.setMajorVersion(classNode.version, 49);
                }

                byte[] enhanceClassByteArray = AsmUtils.toBytes(classNode, loader, classReader);

                // dump the class
                File file = FileUtils.dumpClassIfNecessary(classBeingRedefined, enhanceClassByteArray);
                if (file != null) {
                    affect.addClassDumpFile(file);
                }

                // 成功计数
                affect.classCnt(1);

                return enhanceClassByteArray;
            }
        } catch (
                Exception ex) {
            logger.warn("transform error :" + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 方法加强
     *
     * @param interceptor
     * @param methodProcessor
     * @param loader
     * @param classNode
     * @param methodNode
     * @param className
     */
    private void enhanceMethod(InterceptorProcessor interceptor, MethodProcessor methodProcessor
            , ClassLoader loader, ClassNode classNode, MethodNode methodNode, String className) {
        try {
            List<Location> locations = interceptor.process(methodProcessor);
            for (Location location : locations) {
                if (location instanceof MethodInsnNodeWare) {
                    MethodInsnNodeWare methodInsnNodeWare = (MethodInsnNodeWare) location;
                    MethodInsnNode methodInsnNode = methodInsnNodeWare.methodInsnNode();
                    if (listener instanceof InvokeListener && !isIgnoreListener(methodInsnNode)) {
                        AdviceListenerManager.registerInvokeListener(loader, className,
                                methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, listener);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Enhancer error, class: {}, method: {}, interceptor: {}", classNode.name, methodNode.name, interceptor.getClass().getName(), e);
        }
    }

    /**
     * 是否忽略注册侦听
     *
     * @param methodInsnNode
     * @return
     */
    private boolean isIgnoreListener(MethodInsnNode methodInsnNode) {
        if (this.skipJDKTrace && methodInsnNode.owner.startsWith("java/")) {
            return true;
        }
        // 原始类型的box类型相关的都跳过
        if (AsmOpUtils.isBoxType(Type.getObjectType(methodInsnNode.owner))) {
            return true;
        }
        return false;
    }

    /**
     * 获取过滤器：用于检查是否已插入了spy函数，如果已有则不重复处理
     *
     * @return
     */
    private GroupLocationFilter generateGroupLocationFilter() {
        GroupLocationFilter groupLocationFilter = new GroupLocationFilter();
        LocationFilter enterFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atEnter",
                LocationType.ENTER);
        LocationFilter existFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atExit",
                LocationType.EXIT);
        LocationFilter exceptionFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class),
                "atExceptionExit", LocationType.EXCEPTION_EXIT);

        groupLocationFilter.addFilter(enterFilter);
        groupLocationFilter.addFilter(existFilter);
        groupLocationFilter.addFilter(exceptionFilter);

        LocationFilter invokeBeforeFilter = new InvokeCheckLocationFilter(Type.getInternalName(SpyAPI.class),
                "atBeforeInvoke", LocationType.INVOKE);
        LocationFilter invokeAfterFilter = new InvokeCheckLocationFilter(Type.getInternalName(SpyAPI.class),
                "atAfterInvoke", LocationType.INVOKE_COMPLETED);
        LocationFilter invokeExceptionFilter = new InvokeCheckLocationFilter(Type.getInternalName(SpyAPI.class),
                "atInvokeException", LocationType.INVOKE_EXCEPTION_EXIT);

        groupLocationFilter.addFilter(invokeBeforeFilter);
        groupLocationFilter.addFilter(invokeAfterFilter);
        groupLocationFilter.addFilter(invokeExceptionFilter);
        return groupLocationFilter;
    }

    /**
     * 获取拦截处理器列表
     *
     * @return
     */
    private List<InterceptorProcessor> generateInterceptorProcessor() {
        // 生成增强字节码
        DefaultInterceptorClassParser defaultInterceptorClassParser = new DefaultInterceptorClassParser();

        final List<InterceptorProcessor> interceptorProcessors = new ArrayList<InterceptorProcessor>();

        interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor1.class));
        interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor2.class));
        interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor3.class));

        if (this.tracing) {
            if (!this.skipJDKTrace) {
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceInterceptor1.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceInterceptor2.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceInterceptor3.class));
            } else {
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceExcludeJDKInterceptor1.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceExcludeJDKInterceptor2.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceExcludeJDKInterceptor3.class));
            }
        }
        return interceptorProcessors;
    }

    /**
     * 是否需要过滤的类
     *
     * @param classes 类集合
     */
    private List<Pair<Class<?>, String>> filter(Set<Class<?>> classes) {
        List<Pair<Class<?>, String>> filteredClasses = new ArrayList<Pair<Class<?>, String>>();
        final Iterator<Class<?>> it = classes.iterator();
        while (it.hasNext()) {
            final Class<?> clazz = it.next();
            boolean removeFlag = false;
            if (null == clazz) {
                removeFlag = true;
            } else if (isUnsafeClass(clazz)) {
                filteredClasses.add(new Pair<Class<?>, String>(clazz, "class loaded by Bootstrap Classloader, try to execute `options unsafe true`"));
                removeFlag = true;
            } else {
                Pair<Boolean, String> unsupportedResult = isUnsupportedClass(clazz);
                if (unsupportedResult.getFirst()) {
                    filteredClasses.add(new Pair<Class<?>, String>(clazz, unsupportedResult.getSecond()));
                    removeFlag = true;
                }
            }
            if (removeFlag) {
                it.remove();
            }
        }
        return filteredClasses;
    }

    /**
     * 是否unsafe类
     *
     * @param clazz
     * @return
     */
    private boolean isUnsafeClass(Class<?> clazz) {
        return clazz.getClassLoader() == null;
    }

    /**
     * 是否过滤目前暂不支持的类
     */
    private static Pair<Boolean, String> isUnsupportedClass(Class<?> clazz) {
        if (ClassUtils.isLambdaClass(clazz)) {
            return new Pair<Boolean, String>(Boolean.TRUE, "class is lambda");
        }

        if (clazz.equals(Integer.class)) {
            return new Pair<Boolean, String>(Boolean.TRUE, "class is java.lang.Integer");
        }

        if (clazz.equals(Class.class)) {
            return new Pair<Boolean, String>(Boolean.TRUE, "class is java.lang.Class");
        }

        if (clazz.equals(Method.class)) {
            return new Pair<Boolean, String>(Boolean.TRUE, "class is java.lang.Method");
        }

        if (clazz.isArray()) {
            return new Pair<Boolean, String>(Boolean.TRUE, "class is array");
        }
        return new Pair<Boolean, String>(Boolean.FALSE, "");
    }

    /**
     * 是否需要忽略
     */
    private boolean isIgnore(MethodNode methodNode, String methodName) {
        return null == methodNode || isAbstract(methodNode.access) || !methodName.equals(methodNode.name)
                || "<clinit>".equals(methodNode.name);
    }

    /**
     * 是否抽象属性
     */
    private boolean isAbstract(int access) {
        return (Opcodes.ACC_ABSTRACT & access) == Opcodes.ACC_ABSTRACT;
    }

    public AbstractEnhanceAdviceListener getListener() {
        return listener;
    }

    public String getCommandId() {
        return getListener().getCommandId();
    }

    public String getSessionId() {
        return getListener().getSessionId();
    }

    public Set<Class<?>> getClassesToEnhance() {
        return classesToEnhance;
    }
}
