package com.yzf.greenmall.web;

import com.yzf.greenmall.entity.User;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 指定电话号码发送验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        boolean flag = userService.sendVerifyCode(phone);
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
     * @param type 数据类型 1：用户名 2：手机
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


}
