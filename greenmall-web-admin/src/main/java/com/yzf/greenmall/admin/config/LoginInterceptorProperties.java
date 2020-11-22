package com.yzf.greenmall.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/11/22
 */
@ConfigurationProperties(prefix = "greenmall.interceptor")
public class LoginInterceptorProperties {
    private List<String> interceptorPaths;

    public List<String> getInterceptorPaths() {
        return interceptorPaths;
    }

    public void setInterceptorPaths(List<String> interceptorPaths) {
        this.interceptorPaths = interceptorPaths;
    }
}
