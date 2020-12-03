package com.yzf.greenmall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:邮件配置
 * @author:leo_yuzhao
 * @date:2020/12/3
 */
@ConfigurationProperties(prefix = "greenmall.mail")
@Data
public class MailProperties {
    public String user;
    public String password;
    public String host;
}
