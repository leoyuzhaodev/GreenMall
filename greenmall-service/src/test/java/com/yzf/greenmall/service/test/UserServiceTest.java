package com.yzf.greenmall.service.test;

import com.yzf.greenmall.GreenMallServiceApplication;
import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.service.CategoryService;
import com.yzf.greenmall.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

    @Autowired
    private CategoryService categoryService;

    @Test
    public void fun1() {
        System.out.println(userService.findUserById(1));
    }

    @Test
    public void fun2(){
        List<CategoryTreeBo> categoryTreeBos = categoryService.loadCategoryTreeBo();
        System.out.println(categoryTreeBos);
    }
}
