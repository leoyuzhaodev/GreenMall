package com.yzf.greenmall.bo;

import lombok.Data;

import java.util.Date;

/**
 * @description: 订单详情项
 * @author:leo_yuzhao
 * @date:2020/12/12
 */
@Data
public class OrderDetailItemBo {
    private Long id;
    private Long accountId;
    private Long goodsId;
    private Integer num;
    private Double unitPrice;
    private Byte state;
    private Date createTime;
    private String goodsTitle;
    private String goodsImage; // 单张图片
}
