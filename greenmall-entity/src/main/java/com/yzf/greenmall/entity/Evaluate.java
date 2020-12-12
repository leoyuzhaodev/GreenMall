package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:Evaluate
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
@Table(name = "tb_evaluate")
public class Evaluate implements Serializable {

    public static final Byte VALID_YES = 1; // 订单有效
    public static final Byte VALID_NO = 0; // 订单失效

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long goodsId;
    private Long accountId;
    private Integer score;
    private String content;
    private String images;
    private Date createTime;
    private Byte valid; // 1 有效 0 失效

    @Transient
    private String goodsTitle; // 商品名称
    @Transient
    private String goodsImage; // 商品图片

}
