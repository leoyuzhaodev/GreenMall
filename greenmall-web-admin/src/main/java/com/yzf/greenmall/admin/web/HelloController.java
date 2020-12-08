package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.admin.interceptor.LoginInterceptor;
import com.yzf.greenmall.common.jwt.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:HelloController
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@Controller
public class HelloController {

    /**
     * 跳转到项目首页
     *
     * @return
     */
    @RequestMapping(path = "/")
    public String toHome() {
        return "forward:index.html";
    }

}
