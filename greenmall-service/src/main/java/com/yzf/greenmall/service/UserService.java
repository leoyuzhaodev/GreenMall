package com.yzf.greenmall.service;

import com.yzf.greenmall.mapper.UserMapper;
import com.yzf.greenmall.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: [测试] userMapper
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int i) {
        return userMapper.selectByPrimaryKey(i);
    }
}
