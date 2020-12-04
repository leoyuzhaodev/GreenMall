package com.yzf.greenmall.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yzf.greenmall.common.DoubleSerialize;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description:商品详情
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
public class GoodsIntroduction implements Serializable {
    private Long id; // 商品id
    private String images; // 商品图片
    private String title;// 商品名称
    private String category; // "分类1 分类2 分类3"
    @JsonSerialize(using = DoubleSerialize.class)
    private Double price; // 12.00 价格
    private String subtitle; // 售卖标题
    private Long stock; // 库存
    private List<Map<String, String>> params; // 规格参数[{参数名1：参数值1},{参数名2：参数值2}] 数组
    private String goodsDetail; //商品详情
    private String afterService; //商品详情
    private Long saleMonth; // 月销量
    private Long saleAll; // 总销量
    private Long commentAll; // 累计评价
    private EvaluateBo evaluate; // 全部评价
}
