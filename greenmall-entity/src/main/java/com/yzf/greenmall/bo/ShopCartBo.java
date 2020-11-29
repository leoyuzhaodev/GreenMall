package com.yzf.greenmall.bo;

import lombok.Data;

/**
 * @description:购物车展示
 * @author:leo_yuzhao
 * @date:2020/11/28
 */
@Data
public class ShopCartBo {
    /*
     商品表/商品详情（商品id,商品图片，商品名称，单价,是否下架），数量，商品库存量
     */
    private Long goodsId; // 商品id
    private String goodsImage; // 商品图片
    private String goodsTitle;// 商品名称
    private String goodsSubTitle; // 商品售卖标题
    private Double price; // 单价
    private Boolean available; // 商品下架，删除 available = false，否则 available = true
    private Long num; // 数量
    private Long stock; // 商品库存量

}
