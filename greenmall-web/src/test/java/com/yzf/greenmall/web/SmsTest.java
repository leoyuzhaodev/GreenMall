package com.yzf.greenmall.web;

import com.yzf.greenmall.GreenMallWebApplication;
import com.yzf.greenmall.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/11/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenMallWebApplication.class)
public class SmsTest {

    @Autowired
    private SmsService smsService;

    /**
     * 测试短信发送
     */
    @Test
    public void test1() {
        smsService.testSendMessage("18372538041");
    }

}
