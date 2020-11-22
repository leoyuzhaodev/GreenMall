package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description: UserMapper
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
public interface UserMapper extends Mapper<User> {

    /**
     * 根据密码和登录关键字（手机号/邮箱/用户名）查找用户
     * @param key
     * @return
     */
    @Select("SELECT * FROM `tb_user` where nick_name = #{key} or phone = #{key} or email = #{key}")
    User findUserByLoginKey(@Param("key") String key);
}
