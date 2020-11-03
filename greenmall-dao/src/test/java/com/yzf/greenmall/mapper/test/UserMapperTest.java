package com.yzf.greenmall.mapper.test;

import com.yzf.greenmall.GreenMallDaoApplication;
import com.yzf.greenmall.entity.User;
import com.yzf.greenmall.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:UserMapperTest
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenMallDaoApplication.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void fun1() {
        User user = userMapper.selectByPrimaryKey(1);
        System.out.println(user);
    }


}
