package com.yzf.greenmall.service;

import com.aliyuncs.exceptions.ClientException;
import com.yzf.greenmall.common.NumberUtils;
import com.yzf.greenmall.config.SmsProperties;
import com.yzf.greenmall.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:SmsService短信发送服务
 * @author:leo_yuzhao
 * @date:2020/11/20
 */
@Service
@EnableConfigurationProperties(SmsProperties.class)
public class SmsService {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties smsProperties;

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 短信发送测试
     *
     * @param phone
     */
    public void testSendMessage(String phone) {
        try {
            String code = NumberUtils.generateCode(6);
            this.smsUtils.sendSms(phone, code, smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());
            System.out.println("短信发送成功...");
        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println("短信发送失败...");
        }
    }

    /**
     * 发送短信
     *
     * @param msg
     */
    public void sendMessage(Map<String, String> msg) {
        // 1，获取 验证码 电话号
        String code = msg.get("code");
        String phone = msg.get("phone");
        try {
            // 2，发送短信
            if (StringUtils.isNoneBlank(code) && StringUtils.isNoneBlank(phone)) {
                this.smsUtils.sendSms(phone, code, smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());
            } else {
                throw new RuntimeException("发送短信，电话号码或者验证码为空!");
            }
        } catch (ClientException e) {
            logger.info("SmsService：指定电话发送验证码：phone:{} code:{} 异常：{}", phone, code, e.getMessage());
            e.printStackTrace();
        }
    }
}
