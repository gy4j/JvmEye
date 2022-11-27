package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.core.command.client.ClientInfoCommand;
import com.gy4j.jvm.eye.core.command.client.response.ClientInfoResponse;
import com.gy4j.jvm.eye.core.command.client.vo.ClientInfoVO;
import com.gy4j.jvm.eye.core.util.SeqUtils;
import com.gy4j.jvm.eye.server.helper.CommandHelper;
import com.gy4j.jvm.eye.server.server.ClientChannel;
import com.gy4j.jvm.eye.server.server.EyeServer;
import com.gy4j.jvm.eye.server.vo.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/8-9:09
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("api/client")
public class ApiClientController {
    @Autowired
    private EyeServer eyeServer;

    @RequestMapping("list")
    public ResponseWrapper<List<ClientInfoVO>> list() {
        Map<String, ClientChannel> clientChannels = eyeServer.getClients();
//        Map<String, ClientChannel> clientChannels = mockClients();
        List<ClientInfoVO> clientInfos = new ArrayList<>();
        for (ClientChannel clientChannel : clientChannels.values()) {
            clientInfos.add(clientChannel.getClientInfoVO());
        }
        Collections.sort(clientInfos, new Comparator<ClientInfoVO>() {
            @Override
            public int compare(ClientInfoVO o1, ClientInfoVO o2) {
                if (o1.getClientName().equals(o2.getClientName())) {
                    return o1.getClientId().compareTo(o2.getClientId());
                }
                return o1.getClientName().compareTo(o2.getClientName());
            }
        });
        return ResponseWrapper.ok(clientInfos);
    }

    private Map<String, ClientChannel> mockClients() {
        Map<String, ClientChannel> map = new HashMap<>();
        for (int i = 0; i < 35; i++) {
            ClientChannel clientChannel = new ClientChannel();
            ClientInfoVO clientInfoVO = new ClientInfoVO();
            clientInfoVO.setClientId(SeqUtils.getSeq());
            clientInfoVO.setClientName("appName" + i);
            clientInfoVO.setHost("host-" + SeqUtils.getSeq());
            clientInfoVO.setIp("127.0.0." + i);
            clientInfoVO.setVersion("1.0.0");
            clientChannel.setClientInfoVO(clientInfoVO);
            map.put(clientInfoVO.getClientId(), clientChannel);
        }
        return map;
    }

    @RequestMapping("info")
    public ResponseWrapper<ClientInfoVO> info(@RequestParam String clientId) {
        ClientInfoCommand command = new ClientInfoCommand();
        ClientInfoResponse response = CommandHelper.dealCommand(eyeServer, clientId, command);
        return ResponseWrapper.ok(response.getClientInfo());
    }
}
