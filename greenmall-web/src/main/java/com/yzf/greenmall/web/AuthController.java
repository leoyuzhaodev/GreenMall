package com.yzf.greenmall.web;

import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.common.CookieUtils;
import com.yzf.greenmall.common.jwt.JwtUtils;
import com.yzf.greenmall.config.JwtProperties;
import com.yzf.greenmall.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:AuthController
 * @author:leo_yuzhao
 * @date:2020/11/21
 */
@Controller
@EnableConfigurationProperties(JwtProperties.class)
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 授权
     *
     * @return
     */
    @PostMapping(path = "/accredit")
    public ResponseEntity<Void> authentication(@RequestParam("key") String key,
                                               @RequestParam("password") String password,
                                               HttpServletRequest request,
                                               HttpServletResponse responses) {
        // 1，登录校验，获取授权码：token
        String token = authService.authentication(key, password);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 2，将授权码写入客户端浏览器,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.setCookie(request, responses, jwtProperties.getCookieName(),
                token, jwtProperties.getCookieMaxAge(), null, true);

        return ResponseEntity.ok().build();
    }

    /**
     * 登录验证
     *
     * @return
     */
    @GetMapping(path = "/verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("GM_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse responses) {
        try {
            // 1，从token中获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            // 2，重置授权码的有效时间
            String newToken = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            // 3，将cookie写回用户浏览器
            CookieUtils.setCookie(request, responses, jwtProperties.getCookieName(),
                    newToken, jwtProperties.getCookieMaxAge(), null, true);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
