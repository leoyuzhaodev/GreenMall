package com.yzf.greenmall.service;

import com.yzf.greenmall.common.CodecUtils;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.NumberCalUtil;
import com.yzf.greenmall.common.NumberUtils;
import com.yzf.greenmall.common.jwt.UserInfo;
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
    // 注册验证码前缀
    static final String KEY_PREFIX = "user:code:phone:";
    // 设置支付密码验证码前缀
    static final String KEY_PREFIX_SET_PAY = "user:pay:code:phone:";
    // 绑定手机验证码前缀
    static final String KEY_PREFIX_BIND_PHONE = "user:bp:code:phone:";
    // 邮箱绑定验证码前缀
    static final String KEY_PREFIX_MAIL = "user:code:mail:";
    // 找回密码验证码前缀
    static final String KEY_PREFIX_FIND_PASSWORD = "user:code:findpwd:";

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
     * @param keyType 1: 登录验证码 2：设置支付密码验证码 3：重新绑定手机的验证码 4：找回密码的验证码
     */
    public boolean sendVerifyCode(String phone, int keyType) {
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
            if (keyType == 1) {
                // 登录验证码
                this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            } else if (keyType == 2) {
                // 设置支付密码验证码
                this.redisTemplate.opsForValue().set(KEY_PREFIX_SET_PAY + phone, code, 5, TimeUnit.MINUTES);
            } else if (keyType == 3) {
                // 重新绑定手机的验证码
                this.redisTemplate.opsForValue().set(KEY_PREFIX_BIND_PHONE + phone, code, 5, TimeUnit.MINUTES);
            } else if (keyType == 4) {
                // 找回密码的验证码
                this.redisTemplate.opsForValue().set(KEY_PREFIX_FIND_PASSWORD + phone, code, 5, TimeUnit.MINUTES);
            }
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
        } else if (type == 3) {
            // 验证邮箱
            record = new User();
            record.setEmail(data);
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
        String key = KEY_PREFIX + user.getPhone();
        String redisCode = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            return false;
        }
        this.redisTemplate.delete(key);
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
        // 7，设置默认金额 10000
        user.setPossession(10000D);
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

    /**
     * 判断当前登录用户是否拥有支付密码
     *
     * @param loginUser
     * @return
     */
    public Message hasPayPassword(UserInfo loginUser) {

        // 1，查询当前登录用户的完整信息
        User user = userMapper.selectByPrimaryKey(loginUser.getId());
        if (user == null) {
            throw new RuntimeException("当前登录用户不存在!");
        }
        // 2，判断并返回标识
        if (StringUtils.isBlank(user.getPayPassword())) {
            return new Message(2, "");
        } else {
            return new Message(1, "");
        }
    }

    /**
     * 更新用户密码
     *
     * @param loginUser
     * @param map
     * @return
     */
    public Message updatePassword(UserInfo loginUser, Map<String, String> map) {
        // 1，判断密码是否为空，新旧密码是否一样
        String pwd = map.get("pwd");
        String newPwd = map.get("newPwd");
        if (StringUtils.isBlank(pwd) || StringUtils.isBlank(newPwd)) {
            return new Message(2, "");
        }
        if (pwd.equals(newPwd)) {
            return new Message(3, "");
        }

        // 2，根据id查询出用户数据，并进行原密码比对
        User user = userMapper.selectByPrimaryKey(loginUser.getId());
        String temp = CodecUtils.md5Hex(pwd, user.getSalt());
        if (!temp.equals(user.getPassword())) {
            return new Message(2, "");
        }

        // 3，修改密码
        String salt = CodecUtils.generateSalt();
        String newPassword = CodecUtils.md5Hex(newPwd, salt);
        User user1 = new User();
        user1.setId(loginUser.getId());
        user1.setPassword(newPassword);
        user1.setSalt(salt);
        userMapper.updateByPrimaryKeySelective(user1);
        return new Message(1, "");

    }

    /**
     * 设置支付密码
     *
     * @param loginUser
     * @param user
     * @param code
     * @return
     */
    public Message setPayPassword(UserInfo loginUser, User user, String code) {
        // 1，获取验证码并进行比对
        String key = KEY_PREFIX_SET_PAY + user.getPhone();
        String redisCode = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            return new Message(2, "");
        }
        this.redisTemplate.delete(key);
        // 2，设置支付密码
        User record = new User();
        record.setId(loginUser.getId());
        String salt = CodecUtils.generateSalt();
        String payPassword = CodecUtils.md5Hex(user.getPayPassword(), salt);
        record.setPayPassword(payPassword);
        record.setPaySalt(salt);
        userMapper.updateByPrimaryKeySelective(record);
        return new Message(1, "");
    }

    /**
     * 绑定手机号码
     *
     * @param loginUser
     * @param code1     原手机号验证码
     * @param code2     新手机号验证码
     * @param newPhone  新手机号
     * @return
     */
    public Message bindPhone(UserInfo loginUser, String code1, String code2, String newPhone) {

        // 1，查询登录用户
        User user = userMapper.selectByPrimaryKey(loginUser.getId());

        // 2，获取验证码并进行比对
        String key = KEY_PREFIX_BIND_PHONE + user.getPhone();
        String redisCode = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code1)) {
            return new Message(2, "");
        }
        key = KEY_PREFIX_BIND_PHONE + newPhone;
        redisCode = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code2)) {
            return new Message(2, "");
        }

        // 3，更换手机号
        user.setPhone(newPhone);
        userMapper.updateByPrimaryKeySelective(user);
        return new Message(1, "");
    }

    /**
     * 指定邮箱发送验证码
     *
     * @param email
     * @param keyType 1:登录验证码，2:找回密码的验证码
     * @return
     */
    public boolean sendMailCode(String email, int keyType) {
        String code = null;
        try {
            // 1，生成验证码
            code = NumberUtils.generateCode(6);
            Map<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("email", email);
            // 2，发送消息到消息队列通知信息服务发送短信验证码
            this.amqpTemplate.convertAndSend("greenmall.mail.exchange", "mail.verify.code", map);
            // 3，将验证码存放在redis中，有效时间为5分钟
            if (keyType == 1) {
                // 登录验证码
                this.redisTemplate.opsForValue().set(KEY_PREFIX_MAIL + email, code, 5, TimeUnit.MINUTES);
            } else if (keyType == 2) {
                // 找回密码的验证码
                this.redisTemplate.opsForValue().set(KEY_PREFIX_FIND_PASSWORD + email, code, 5, TimeUnit.MINUTES);
            }
            return true;
        } catch (AmqpException e) {
            e.printStackTrace();
            logger.info("UserService：指定邮箱发送验证码：email:{} code:{} 异常：{}", email, code, e.getMessage());
        }
        return false;
    }

    /**
     * 绑定邮箱
     *
     * @param loginUser
     * @param user      {email:""}
     * @param code      验证码
     * @return
     */
    public Message bindEmail(UserInfo loginUser, User user, String code) {
        // 1，查询登录用户
        User curUser = userMapper.selectByPrimaryKey(loginUser.getId());

        // 2，获取验证码并进行比对
        String key = KEY_PREFIX_MAIL + user.getEmail();
        String redisCode = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            return new Message(2, "");
        }

        // 3，绑定邮箱
        curUser.setEmail(user.getEmail());
        userMapper.updateByPrimaryKeySelective(curUser);
        return new Message(1, "");
    }

    /**
     * 找回密码发送验证码
     *
     * @param loginFlag
     * @return
     */
    public Message findPwdCode(String loginFlag) {

        if (StringUtils.isBlank(loginFlag)) {
            return new Message(2, "用户身份标识为空!");
        }
        // 1，根据 用户名/电话号码/邮箱 进行查找
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("phone", loginFlag);
        criteria.orEqualTo("nickName", loginFlag);
        criteria.orEqualTo("email", loginFlag);
        User user = userMapper.selectOneByExample(example);
        if (user == null) {
            return new Message(2, "系统不存在此身份标识的用户!");
        }

        // 2,发送验证码
        if (StringUtils.isNoneBlank(user.getEmail())) {
            // 给邮箱发送验证码
            sendMailCode(user.getEmail(), 2);
        } else {
            // 给手机发送验证码
            sendVerifyCode(user.getPhone(), 4);
        }

        return new Message(1, user.getId() + "");
    }

    /**
     * 找回密码
     *
     * @param user
     * @param code
     * @return
     */
    public Message findBackPwd(User user, String code) {

        if (user.getId() == null || StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(code)) {
            return new Message(2, "该标识下的用户不存在或者验证码，密码为空！");
        }

        // 1，查找用户数据根据用户id
        User sysUser = userMapper.selectByPrimaryKey(user.getId());
        if (sysUser == null) {
            return new Message(2, "该标识下的用户不存在！");
        }

        // 2，比对验证码
        String key = null;
        if (StringUtils.isNotBlank(sysUser.getEmail())) {
            key = KEY_PREFIX_FIND_PASSWORD + sysUser.getEmail();
        } else {
            key = KEY_PREFIX_FIND_PASSWORD + sysUser.getPhone();
        }
        String redisCode = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            return new Message(2, "验证码错误!");
        }

        // 3，重置密码
        String salt = CodecUtils.generateSalt();
        String newPassword = CodecUtils.md5Hex(user.getPassword(), salt);
        sysUser.setPassword(newPassword);
        sysUser.setSalt(salt);
        userMapper.updateByPrimaryKeySelective(sysUser);

        return new Message(1, "");
    }

    /**
     * 查询用户账户余额
     *
     * @param id
     * @return
     */
    public Double findUserPossession(Long id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new RuntimeException("账户id对应的用户不存在!");
        }
        return user.getPossession();
    }

    /**
     * 从指定账户扣钱
     *
     * @param id
     * @param totalPrice
     * @return
     */
    public boolean cost(Long id, Double totalPrice) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new RuntimeException("账户id对应的用户不存在!");
        }
        if (user.getPossession() - totalPrice < 0) {
            return false;
        }
        // 扣钱并更新账户
        user.setPossession(NumberCalUtil.subtract(user.getPossession(), totalPrice));
        userMapper.updateByPrimaryKeySelective(user);
        return true;
    }
}
