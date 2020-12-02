package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description:用户收货地址
 * @author:leo_yuzhao
 * @date:2020/12/1
 */
@Data
@Table(name = "tb_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private String consignee; // 收货人
    private String phone; // 收货人电话
    private Long province;// 一级行政区编号
    private Long county;// 二级级行政区编号
    private Long town;// 三级行政区编号
    private String addressDetail; // 详细地址
    private Byte isDefault; // 1：默认 2：非默认
    private Byte valid; // 是否有效：1：有效 0：无效
    @Transient
    private String fullAddress;
}
