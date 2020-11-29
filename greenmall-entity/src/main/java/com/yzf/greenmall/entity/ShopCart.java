package com.yzf.greenmall.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:购物车
 * @author:leo_yuzhao
 * @date:2020/11/28
 */
@Data
public class ShopCart implements Serializable {
    public Long accountId; // 账户id
    public Long goodsId; // 商品id
    public Long num; // 数量

    public ShopCart() {
    }

    public ShopCart(Long goodsId, Long num) {
        this.goodsId = goodsId;
        this.num = num;
    }
}
