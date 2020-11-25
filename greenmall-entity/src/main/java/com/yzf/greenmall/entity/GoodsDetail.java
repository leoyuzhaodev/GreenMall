package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:商品详情信息
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Data
@Table(name = "tb_goods_detail")
public class GoodsDetail implements Serializable {
    @Id
    private Long goodsId; // 商品id
    private String images; // 商品图片以英文逗号分割
    private Double price; // 商品价格，注意通用 Mapper 不支持基本数据类型
    private String params; // 商品规格参数，存放格式为：{pId1:pVal1,pId2:pVal2}
    private String description; // 商品描述
    private String packingList; // 商品打包
    private String afterService; // 售后服务
    private Long stock; // 商品库存

}
