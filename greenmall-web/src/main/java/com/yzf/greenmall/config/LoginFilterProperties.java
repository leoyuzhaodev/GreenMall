package com.yzf.greenmall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "greenmall.filter")
public class LoginFilterProperties {

    private List<String> filterPaths;

    public List<String> getFilterPaths() {
        return filterPaths;
    }

    public void setFilterPaths(List<String> filterPaths) {
        this.filterPaths = filterPaths;
    }
}