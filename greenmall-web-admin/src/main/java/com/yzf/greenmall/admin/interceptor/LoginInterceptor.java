package com.yzf.greenmall.admin.interceptor;

import com.yzf.greenmall.bo.UserInfo;
import com.yzf.greenmall.common.CookieUtils;
import com.yzf.greenmall.common.jwt.JwtUtils;
import com.yzf.greenmall.config.JwtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:登录拦截器，加载用户信息
 * @author:leo_yuzhao
 * @date:2020/11/21
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);


    // 定义一个线程域，存放登录用户
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 1，获取token
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
            // 2，根据token获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            // 3，将用户信息存放在 ThreadLocal<UserInfo> 中
            tl.set(userInfo);
            return true;
        } catch (Exception e) {
            LOGGER.info("LoginInterceptor：用户信息获取失败，拦截请求。");
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
        tl.remove();
    }

    /**
     * 获取当前线程域中的用户信息
     *
     * @return
     */
    public static UserInfo getLoginUser() {
        return tl.get();
    }

}
