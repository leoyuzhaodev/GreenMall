package com.yzf.greenmall.admin.config;

import com.yzf.greenmall.admin.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:自定义SpringMVC的配置
 * @author:leo_yuzhao
 * @date:2020/11/21
 */
@Configuration
@EnableConfigurationProperties(LoginInterceptorProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptorProperties interceptorProperties;

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns(interceptorProperties.getInterceptorPaths());
    }
}
