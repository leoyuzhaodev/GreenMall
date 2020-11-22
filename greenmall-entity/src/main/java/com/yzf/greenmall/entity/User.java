package com.yzf.greenmall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Data
@Table(name = "tb_user")
public class User implements Serializable {

    public static final Integer USER_VALID_YES = 1;
    public static final Integer USER_VALID_NO = 0;
    public static final Integer USER_STATE_NOT_INIT = 1;
    public static final Integer USER_STATE_INIT = 2;
    public static final Integer USER_STATE_PP = 4;
    public static final Integer USER_NAME_DEFAULT_LEN = 10;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickName;
    // 用户性别：1：男，0：女
    private Integer sex;
    private Date birthday;
    private String phone;
    private String email;
    private Date createDate;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;
    @JsonIgnore
    private String payPassword;
    @JsonIgnore
    private String paySalt;
    // 用户状态：1：非禁用 0：禁用
    private Integer valid;
    // 状态 1：未初始化，刚刚注册 2:初始化完成 4:支付密码设置完成
    private Integer state;

}
