package com.gy4j.jvm.eye.agent;

import com.gy4j.jvm.eye.client.EyeClient;
import com.gy4j.jvm.eye.core.util.StringUtils;
import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.Instrumentation;
import java.security.CodeSource;
import java.util.Arrays;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class EyeBootstrap {
    /**
     * vmoption:  -javaagent:G:\xx\jvm-eye-agent-shade.jar=DemoSimple,127.0.0.1,5555
     *
     * @param args
     * @param inst
     */
    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    /**
     * vmoption: -javaagent:G:\xx\jvm-eye-agent-shade.jar=DemoSimple,127.0.0.1,5555
     *
     * @param args
     * @param inst
     */
    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    private static synchronized void main(String args, final Instrumentation inst) {
        System.out.println("args:" + args);
        if (StringUtils.isBlank(args)) {
            throw new IllegalArgumentException("args 不能为空！args格式为：appName,host,port");
        }
        String[] ps = args.split(",");
        if (ps.length != 3) {
            throw new IllegalArgumentException("args 格式错误！args格式为：appName|host|port");
        }
        new EyeClient(inst, ps[0], ps[1], Integer.valueOf(ps[2]));
    }

    /**
     * java -jar jvm-eye-agent-shade.jar pid appName host port agentPath
     * ex: java -Xbootclasspath/a:"%JAVA_HOME%\lib\tools.jar" -jar jvm-eye-agent-shade.jar 13424 MathGame 127.0.0.1 5555 G:\arthas\git\eye\jvm-eye-agent\target\jvm-eye-agent-shade.jar
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println("args 格式为：pid appName host(默认值为localhost) port(默认值为5555) agentPath");
            System.out.println("当前参数：" + Arrays.asList(args));

            String pid = args[0];
            String appName = args[1];
            String host = args[2];
            String port = args[3];
            String agentPath = null;
            if (args.length > 4) {
                agentPath = args[5];
            } else {
                CodeSource codeSource = EyeBootstrap.class.getProtectionDomain().getCodeSource();
                System.out.println("agentPath:" + codeSource.getLocation().getFile().substring(1));

                String jarPath = codeSource.getLocation().getFile();
                if (jarPath.endsWith(".jar")) {
                    agentPath = jarPath.substring(1);
                }
            }

            VirtualMachine virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent(agentPath,
                    appName + "," + host + "," + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
