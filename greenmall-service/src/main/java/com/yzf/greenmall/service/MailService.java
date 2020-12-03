package com.yzf.greenmall.service;

import cn.hutool.extra.mail.Mail;
import com.yzf.greenmall.config.MailProperties;
import com.yzf.greenmall.config.SmsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * 发邮件工具类
 */
@Service
@Transactional
@EnableConfigurationProperties(MailProperties.class)
public class MailService {

    @Autowired
    private MailProperties mailProperties;

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * @param to    收件人邮箱
     * @param text  邮件正文
     * @param title 标题
     */
    private boolean sendMail(String to, String text, String title) {
        try {
            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", mailProperties.getHost());

            // 发件人的账号
            props.put("mail.user", mailProperties.getUser());
            //发件人的密码
            props.put("mail.password", mailProperties.getPassword());

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            logger.info("发给 {} 的邮件：{}，发送成功!", to, text);
            return true;
        } catch (Exception e) {
            logger.info("SmsService：指定邮箱发送信息：email:{} message:{} 异常：{}", to, text, e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送邮件
     *
     * @param msg {code:"",email:""}
     */
    public void sendMessage(Map<String, String> msg) {
        // 1，验证
        if (CollectionUtils.isEmpty(msg)) {
            throw new RuntimeException("邮件发送信息为空，无法发送邮件!");
        }
        String code = msg.get("code");
        String email = msg.get("email");
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(email)) {
            throw new RuntimeException("邮件发送信息为空，无法发送邮件!");
        }
        // 2，发送邮件
        sendMail(email, "您的验证码是：【" + code + "】，验证码在5分钟之内有效。", "绿色商城邮箱验证码");
    }

}