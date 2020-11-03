package com.yzf.greenmall.service.test;

import com.yzf.greenmall.GreenMallServiceApplication;
import com.yzf.greenmall.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:UserServiceTest
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenMallServiceApplication.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void fun1() {
        System.out.println(userService.findUserById(1));
    }
}
