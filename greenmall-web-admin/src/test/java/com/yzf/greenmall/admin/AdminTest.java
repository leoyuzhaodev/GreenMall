package com.yzf.greenmall.admin;

import com.yzf.greenmall.common.CodecUtils;
import org.junit.Test;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/11/22
 */
public class AdminTest {
    @Test
    public void fun1() {
        // 账号：admin 密码：gmadmin
        String salt = CodecUtils.generateSalt();
        String admin = CodecUtils.md5Hex("gmadmin", salt);
        System.out.println("salt:" + salt + ",admin:" + admin);
    }
}
