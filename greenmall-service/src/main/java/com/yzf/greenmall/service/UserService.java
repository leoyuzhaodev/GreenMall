package com.yzf.greenmall.service;

import com.yzf.greenmall.common.CodecUtils;
import com.yzf.greenmall.common.NumberUtils;
import com.yzf.greenmall.mapper.UserMapper;
import com.yzf.greenmall.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.codecs.CodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
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

    /**
     * 检查用户数据的正确性
     *
     * @param data 需要检测的数据
     * @param type 数据类型 1：用户名 2：手机
     * @return false:不可用 true:可用
     */
    public Boolean checkUserData(String data, Integer type) {
        User record = null;
        if (type == 1) {
            // 验证用户名
            record = new User();
            record.setNickName(data);
        } else if (type == 2) {
            // 验证手机
            record = new User();
            record.setPhone(data);
        } else {
            return null;
        }
        int i = userMapper.selectCount(record);
        return i > 0 ? false : true;
    }

    /**
     * 注册用户
     *
     * @param user 用户数据
     * @param code 验证码
     * @return
     */
    public boolean register(User user, String code) {
        // 1，获取验证码并进行比对
        String redisCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            return false;
        }
        // 2，初始化用户昵称
        user.setNickName(NumberUtils.generateCode(User.USER_NAME_DEFAULT_LEN));
        // 3，初始化账号创建时间
        user.setCreateDate(new Date());
        // 4，密码加盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        // 5，初始化账号是否可用
        user.setValid(User.USER_VALID_YES);
        // 6，初始化账号状态
        user.setState(User.USER_STATE_NOT_INIT);
        int i = userMapper.insertSelective(user);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据密码和登录关键字（手机号/邮箱/用户名）查找用户
     *
     * @param key
     * @param password
     * @return
     */
    public User findUserByKeyAndPwd(String key, String password) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(password)) {
            return null;
        }
        User record = new User();
        // 1，根据 用户名/电话号码/邮箱 进行查找
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("phone", key);
        criteria.orEqualTo("nickName", key);
        criteria.orEqualTo("email", key);
        User user = userMapper.selectOneByExample(example);
        if (user == null) {
            return null;
        }
        // 2，比对密码
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            return null;
        }
        return user;
    }

    /**
     * 根据用户id加载当前登录用户的用户信息
     *
     * @param id
     * @return
     */
    public User findUser(Long id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new RuntimeException("用户信息加载异常!");
        }
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    public boolean update(User user) {

        try {
            // 1，查看用户是否存在
            User record = new User();
            record.setId(user.getId());
            int i = userMapper.selectCount(record);
            if (1 != i) {
                throw new RuntimeException("更新用户信息：id对应的用户不存在");
            }
            // 更新信息：选择性更新
            user.setEmail(null);
            user.setPhone(null);
            userMapper.updateByPrimaryKeySelective(user);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
