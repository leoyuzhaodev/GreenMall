package com.yzf.greenmall.filter;

import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.common.CookieUtils;
import com.yzf.greenmall.common.jwt.JwtUtils;
import com.yzf.greenmall.config.JwtProperties;
import com.yzf.greenmall.config.LoginFilterProperties;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:登录过滤器
 * @author:leo_yuzhao
 * @date:2020/11/22
 */
@Component
@EnableConfigurationProperties({LoginFilterProperties.class, JwtProperties.class})
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
public class LoginFilter implements Filter {

    @Autowired
    private LoginFilterProperties loginFilterProperties;

    @Autowired
    private JwtProperties jwtProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // 获取路径
        String uri = req.getRequestURI();
        // 根据路径判断是否放行
        if (!CollectionUtils.isEmpty(loginFilterProperties.getFilterPaths())) {
            for (String interceptorPath : loginFilterProperties.getFilterPaths()) {
                // 进行拦截
                if (uri.contains(interceptorPath)) {
                    // 判断用户是否登录
                    if (!isUserLogin(req)) {
                        // 没有登录不放行，返回相关提示信息
                        resp.setContentType("text/html;charset=UTF-8");
                        resp.getWriter().write("请登录之后进行访问。");
                        // 401 表明用户没有权限访问
                        resp.setStatus(401);
                        return;
                    }
                    break;
                }
            }
        }
        // 放行代码
        chain.doFilter(request, response);
    }

    /**
     * 判断用户是否登录
     *
     * @param request
     * @return
     */
    public boolean isUserLogin(HttpServletRequest request) {
        try {
            // 1，获取token
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
            // 2，根据token获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            return true;
        } catch (Exception e) {
            LOGGER.info("LoginFilter：用户信息获取失败，拦截请求。");
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
