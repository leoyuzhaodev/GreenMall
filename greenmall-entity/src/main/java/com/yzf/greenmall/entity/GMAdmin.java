package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description:管理员信息
 * @author:leo_yuzhao
 * @date:2020/11/22
 */
@Data
@Table(name = "tb_gm_admin")
public class GMAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickName;
    private String password;
    private String salt;
}
