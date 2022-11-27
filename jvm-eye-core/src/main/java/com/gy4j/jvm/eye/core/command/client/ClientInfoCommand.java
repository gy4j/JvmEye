package com.gy4j.jvm.eye.core.command.client;

import com.alibaba.bytekit.utils.IOUtils;
import com.gy4j.jvm.eye.core.client.IClient;
import com.gy4j.jvm.eye.core.command.AbstractCommand;
import com.gy4j.jvm.eye.core.command.client.response.ClientInfoResponse;
import com.gy4j.jvm.eye.core.command.client.vo.ClientInfoVO;
import com.gy4j.jvm.eye.core.constant.EyeConstants;
import com.gy4j.jvm.eye.core.response.IResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class ClientInfoCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(ClientInfoCommand.class);

    private static final String VERSION_LOCATION = "/res/version.txt";

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ClientInfoResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        // 获取并设置服务器的名称和IP信息
        InetAddress inetAddress = null;
        String host = EyeConstants.UNKNOWN;
        String ip = EyeConstants.UNKNOWN;
        try {
            inetAddress = InetAddress.getLocalHost();
            host = inetAddress.getHostName();
            ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("get host,ip exception", e);
        }
        ClientInfoVO clientInfo = new ClientInfoVO();
        clientInfo.setHost(host);
        clientInfo.setIp(ip);
        clientInfo.setVersion(getVersion());


        // 设置客户端名称和ID
        clientInfo.setClientName(client.getClientName());
        clientInfo.setClientId(client.getClientId());

        ClientInfoResponse clientInfoResponse = new ClientInfoResponse();
        clientInfoResponse.setClientInfo(clientInfo);
        return clientInfoResponse;
    }

    private String getVersion() {
        InputStream versionInputStream = ClientInfoCommand.class.getResourceAsStream(VERSION_LOCATION);
        String version = EyeConstants.UNKNOWN;
        if (versionInputStream != null) {
            try {
                version = IOUtils.toString(versionInputStream).trim();
                return version;
            } catch (IOException e) {
                logger.warn("{} get version error: {}", VERSION_LOCATION, e.getMessage());
            }
        }
        String implementationVersion = ClientInfoCommand.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            version = implementationVersion;
        }
        return version;
    }
}
