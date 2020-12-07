package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description:订单详情
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Data
@Table(name = "tb_order_detail")
public class OrderDetail {

    public static final Integer STATE_NORMAL = 10; // 正常
    public static final Integer STATE_REFUNDING = 20; // 退款中
    public static final Integer STATE_REFUNDED = 30; // 完成退款

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long goodsId;
    private Double unitPrice;
    private Integer num;
    private Integer state; // 10:正常 20:退款中 30:完成退款

    @Transient
    private String goodsTitle; // 商品名称
    @Transient
    private String goodsImage; // 商品图片


}
