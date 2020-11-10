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
    private String title;// 标题
    private String subTitle;// 子标题
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目

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
}
