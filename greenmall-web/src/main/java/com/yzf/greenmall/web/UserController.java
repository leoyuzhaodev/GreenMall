package com.yzf.greenmall.web;

import com.yzf.greenmall.common.CookieUtils;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.config.JwtProperties;
import com.yzf.greenmall.entity.User;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @description:UserController
 * @author:leo_yuzhao
 * @date:2020/11/20
 */
@Controller
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 指定电话号码发送验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        boolean flag = userService.sendVerifyCode(phone, 1);
        if (flag) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 指定电话号码发送 更改支付密码 验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("/auth/payCode")
    public ResponseEntity<Void> sendVerifyPayCode(@RequestParam("phone") String phone) {
        boolean flag = userService.sendVerifyCode(phone, 2);
        if (flag) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 指定电话号码发送 绑定手机 验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("/auth/bindPhoneCode")
    public ResponseEntity<Void> sendBindPhoneCode(@RequestParam("phone") String phone) {
        boolean flag = userService.sendVerifyCode(phone, 3);
        if (flag) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 检查用户数据的正确性
     *
     * @param data 需要检测的数据
     * @param type 数据类型 1：用户名 2：手机 3：邮箱
     * @return false:不可用 true:可用
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable(name = "data", required = true) String data,
                                                 @PathVariable(name = "type", required = true) Integer type) {
        Boolean flag = this.userService.checkUserData(data, type);
        if (flag == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(flag);
        }
    }

    /**
     * 注册用户
     *
     * @param user 用户数据
     * @param code 验证码
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(User user, @RequestParam("code") String code) {
        boolean flag = userService.register(user, code);
        if (flag) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/hello")
    public ResponseEntity<Void> hello() {
        System.out.println("测试登录拦截...");
        System.out.println(LoginInterceptor.getLoginUser().getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * 加载当前登录用户的信息
     *
     * @return
     */
    @GetMapping(path = "/auth/queryUser")
    public ResponseEntity<User> queryUser() {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            User user = userService.findUser(loginUser.getId());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 注册用户
     *
     * @param user 用户数据
     * @return
     */
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody User user) {
        boolean flag = userService.update(user);
        if (flag) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * 判断当前登录用户是否拥有支付密码
     *
     * @return
     */
    @GetMapping(path = "/auth/isHasPayPassword")
    public ResponseEntity<Message> isHasPayPassword() {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            Message message = userService.hasPayPassword(loginUser);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新密码
     *
     * @return
     */
    @PostMapping(path = "/auth/updatePassword")
    public ResponseEntity<Message> updatePassword(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody Map<String, String> map) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            Message message = userService.updatePassword(loginUser, map);
            // 退出登录
            logOut(request, response);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 退出登录
     *
     * @return
     */
    @GetMapping(path = "/auth/logout")
    public ResponseEntity<Void> userLogout(HttpServletRequest request,
                                           HttpServletResponse response) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            // 退出登录
            logOut(request, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * 退出登录用户浏览器的cookie置空
     *
     * @param request
     * @param response
     */
    public void logOut(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(),
                "", jwtProperties.getCookieMaxAge(), null, true);
    }

    /**
     * 设置支付密码
     *
     * @param user
     * @return
     */
    @PostMapping(path = "/auth/setPayPassword")
    public ResponseEntity<Message> setPayPassword(User user, @RequestParam("code") String code) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            Message message = userService.setPayPassword(loginUser, user, code);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 重新绑定手机号码
     *
     * @param code1
     * @param code2
     * @param newPhone
     * @return
     */
    @PostMapping(path = "/auth/bindPhone")
    public ResponseEntity<Message> bindPhone(@RequestParam("code1") String code1,
                                             @RequestParam("code2") String code2,
                                             @RequestParam("newPhone") String newPhone) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            Message message = userService.bindPhone(loginUser, code1, code2, newPhone);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 指定电话号码发送验证码
     *
     * @param email
     * @return
     */
    @PostMapping("/mailCode")
    public ResponseEntity<Void> sendMailCode(@RequestParam("email") String email) {
        boolean flag = userService.sendMailCode(email, 1);
        if (flag) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 绑定邮箱
     *
     * @param user 用户数据
     * @param code 验证码
     * @return
     */
    @PostMapping("/auth/bindEmail")
    public ResponseEntity<Message> bindEmail(User user, @RequestParam("code") String code) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            if (loginUser == null) {
                throw new RuntimeException("用户信息加载异常!");
            }
            Message message = userService.bindEmail(loginUser, user, code);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * 找回密码发送验证码
     *
     * @param loginFlag
     * @return
     */
    @PostMapping(path = "/findPwdCode")
    public ResponseEntity<Message> findPwdCode(@RequestParam("loginFlag") String loginFlag) {
        try {
            Message message = userService.findPwdCode(loginFlag);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 找回密码
     *
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/findBackPwd")
    public ResponseEntity<Message> findBackPwd(User user, @RequestParam("code") String code) {
        try {
            Message message = userService.findBackPwd(user, code);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
