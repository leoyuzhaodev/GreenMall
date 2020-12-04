package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description:订单
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Data
@Table(name = "tb_order")
public class Order {
    public static final Integer STATE_PAYED_SEND = 10; // 完成付款 = 待发货
    public static final Integer STATE_SENT_SIGN = 20; // 已发货 = 代签收
    public static final Integer STATE_FINISHED = 30; // 交易完成
    public static final Integer STATE_CLOSED = 40; // 交易关闭
    public static final Byte VALID_YES = 1; // 订单有效
    public static final Byte VALID_NO = 0; // 订单失效

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private Long addressId;
    private Long logisticsId; // 物流id
    private String logisticsFlag; // 物流公司标识 持申通快递:STO、圆通速递:YTO、百世快递:HTKY、天天快递:HHTT
    private Date createTime;
    private Integer state; // 状态
    private Double totalPrice; // 总价格
    private Byte valid; // 1 有效 0 失效

}
