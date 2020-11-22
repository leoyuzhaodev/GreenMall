package com.yzf.greenmall.interceptor;

import com.yzf.greenmall.bo.UserInfo;
import com.yzf.greenmall.common.CookieUtils;
import com.yzf.greenmall.common.jwt.JwtUtils;
import com.yzf.greenmall.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @description:登录拦截器
 * @author:leo_yuzhao
 * @date:2020/11/21
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            // 1，获取token
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
            // 2，根据token获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3，响应相关信息给前台
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("请登录之后进行访问。");
        response.setStatus(401);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
