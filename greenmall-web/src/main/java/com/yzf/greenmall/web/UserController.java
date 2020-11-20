package com.yzf.greenmall.web;

import com.yzf.greenmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/sendVerifyCode")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        boolean flag = userService.sendVerifyCode(phone);
        if (flag) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
