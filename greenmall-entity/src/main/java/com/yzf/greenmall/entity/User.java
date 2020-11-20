package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: User
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@Table(name = "tb_user")
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String nickName;
    // 用户性别：1：男，0：女
    private Integer sex;
    private Date birthday;
    private String phone;
    private String email;
    private Date createDate;
    private String password;
    private String salt;
    private String payPassword;
    private String paySalt;
    // 用户状态：1：非禁用 0：禁用
    private Integer valid;
    // 状态 1：未初始化，刚刚注册 2:初始化完成 4:支付密码设置完成
    private Integer state;

}
