package com.yzf.greenmall.web;

import com.yzf.greenmall.entity.User;
import com.yzf.greenmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @description:HelloController
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@RestController
public class HelloController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/hello")
    public String hello() {
        User user = userService.findUserById(1);
        return "hello:" + user.getName();
    }

    @RequestMapping(path = "/hello1")
    public ModelAndView hello1() {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserById(1);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("user");
        return modelAndView;
    }
}
