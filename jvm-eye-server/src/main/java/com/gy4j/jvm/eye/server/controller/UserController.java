package com.gy4j.jvm.eye.server.controller;

import com.gy4j.jvm.eye.server.helper.SessionHelper;
import com.gy4j.jvm.eye.server.vo.ResponseWrapper;
import com.gy4j.jvm.eye.server.vo.UserVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/14-20:20
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @RequestMapping("info")
    public ResponseWrapper<UserVO> info() {
        UserVO userVO = new UserVO();
        userVO.setId("001");
        userVO.setUsername("admin");
        userVO.setSessionId(SessionHelper.getSessionId());
        SessionHelper.setSessionUser(userVO);
        return ResponseWrapper.ok(userVO);
    }
}
