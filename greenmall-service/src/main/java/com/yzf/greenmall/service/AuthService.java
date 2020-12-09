package com.yzf.greenmall.service;

import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.common.jwt.JwtUtils;
import com.yzf.greenmall.config.JwtProperties;
import com.yzf.greenmall.entity.GMAdmin;
import com.yzf.greenmall.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:授权服务
 * @author:leo_yuzhao
 * @date:2020/11/21
 */
@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private GMAdminService gmAdminService;

    @Autowired
    private JwtProperties jwtProperties;

    static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    /**
     * 校验用户登录，并生成授权码
     *
     * @param key
     * @param password
     * @return token
     */
    public String authentication(String key, String password) {
        try {
            // 1，查找用户
            User user = userService.findUserByKeyAndPwd(key, password);
            if (user == null || user.getValid() == User.USER_VALID_NO) {
                return null;
            }
            // 2，根据用户信息生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getNickName()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("为用户授权出现异常：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 为Admin颁发认证
     *
     * @param admin
     * @return
     */
    public String authenticationAdmin(GMAdmin admin) {

        try {

            // 1，根据昵称查询用户
            GMAdmin gmAdmin = gmAdminService.findGMAdmin(admin);
            if (gmAdmin == null) {
                return null;
            }
            // 2，根据用户信息生成token
            String token = JwtUtils.generateToken(new UserInfo(gmAdmin.getId(), gmAdmin.getNickName()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
