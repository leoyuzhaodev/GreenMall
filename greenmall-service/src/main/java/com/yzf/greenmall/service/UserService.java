package com.yzf.greenmall.service;

import com.yzf.greenmall.common.NumberUtils;
import com.yzf.greenmall.mapper.UserMapper;
import com.yzf.greenmall.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: userMapper
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 根据id查找用户
     *
     * @param i
     * @return
     */
    public User findUserById(int i) {
        return userMapper.selectByPrimaryKey(i);
    }

    /**
     * 指定电话发送验证码
     *
     * @param phone
     */
    public boolean sendVerifyCode(String phone) {
        String code = null;
        try {
            // 1，生成验证码
            code = NumberUtils.generateCode(6);
            Map<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("phone", phone);
            // 2，发送消息到消息队列通知信息服务发送短信验证码
            this.amqpTemplate.convertAndSend("greenmall.sms.exchange", "sms.verify.code", map);
            // 3，将验证码存放在redis中，有效时间为5分钟
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            e.printStackTrace();
            logger.info("UserService：指定电话发送验证码：phone:{} code:{} 异常：{}", phone, code, e.getMessage());
        }
        return false;
    }
}
