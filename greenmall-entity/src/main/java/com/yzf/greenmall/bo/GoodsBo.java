package com.yzf.greenmall.bo;

import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Data
public class GoodsBo extends GoodsDetail implements Serializable {
    private Long id; // 商品id
    private String title;// 标题
    private String subTitle;// 子标题
    private Long cid1;// 1级分类
    private Long cid2;// 2级分类
    private Long cid3;// 3级分类

    /**
     * 构建商品信息且初始化商品相关参数
     *
     * @return
     */
    public Goods generateGoods() {
        Goods goods = new Goods(this.title, this.subTitle, this.cid1, this.cid2, this.cid3);
        goods.setSaleable(true);
        goods.setCreateTime(new Date());
        goods.setLastUpdateTime(new Date());
        goods.setValid(true);
        goods.setSalesVolume(0);
        return goods;
    }

    /**
     * 构建待编辑商品信息
     *
     * @return
     */
    public Goods generateEditGoods() {
        Goods goods = new Goods(this.title, this.subTitle, this.cid1, this.cid2, this.cid3);
        goods.setId(id);
        goods.setLastUpdateTime(new Date());
        return goods;
    }


    /**
     * 生成 GoodsBo
     *
     * @param goods
     * @param goodsDetail
     * @return
     */
    public static GoodsBo generateGoodsBo(Goods goods, GoodsDetail goodsDetail) {
        GoodsBo goodsBo = new GoodsBo();
        goodsBo.setId(goods.getId());
        goodsBo.setTitle(goods.getTitle());
        goodsBo.setSubTitle(goods.getSubTitle());
        goodsBo.setCid1(goods.getCid1());
        goodsBo.setCid2(goods.getCid2());
        goodsBo.setCid3(goods.getCid3());
        goodsBo.setGoodsId(goods.getId());
        goodsBo.setImages(goodsDetail.getImages());
        goodsBo.setPrice(goodsDetail.getPrice());
        goodsBo.setParams(goodsDetail.getParams());
        goodsBo.setDescription(goodsDetail.getDescription());
        goodsBo.setPackingList(goodsDetail.getPackingList());
        goodsBo.setAfterService(goodsDetail.getAfterService());
        return goodsBo;
    }
}
