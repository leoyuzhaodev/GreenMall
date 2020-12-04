package com.yzf.greenmall.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:订单提交bo
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Data
public class SubmitOrderBo implements Serializable {
    private Long addressId;
    private Long goodsId;
    private Double unitPrice;
    private Integer num;
}
