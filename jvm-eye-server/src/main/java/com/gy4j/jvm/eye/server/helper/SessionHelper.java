package com.gy4j.jvm.eye.server.helper;

import com.gy4j.jvm.eye.server.vo.UserVO;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/16-11:16
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
public class SessionHelper {
    private static final String SESSION_USER = "USER";

    public static UserVO getSessionUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request.getSession().getAttribute(SESSION_USER) != null) {
            return (UserVO) request.getSession().getAttribute(SESSION_USER);
        }
        return null;
    }

    public static void setSessionUser(UserVO session) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute(SESSION_USER, session);
    }

    public static String getSessionUserId() {
        UserVO user = getSessionUser();
        if (user == null) {
            return null;
        } else {
            return user.getId();
        }
    }

    public static String getSessionId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getSession(true).getId();
    }
}
