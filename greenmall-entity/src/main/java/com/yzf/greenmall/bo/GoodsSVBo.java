package com.yzf.greenmall.bo;

import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @description:商品销量BO
 * @author:leo_yuzhao
 * @date:2020/12/11
 */
@Data
public class GoodsSVBo {
    private Long goodsId;
    private String goodsImage;
    private String goodsTitle;
    private Double goodsPrice;
    private Date createTime;
    private Long topNumber;
    private Long saleVolume;

    public static GoodsSVBo generateGoodsSVBo(Goods goods, GoodsDetail goodsDetail) {
        GoodsSVBo goodsSVBo = new GoodsSVBo();
        // goodsId
        goodsSVBo.setGoodsId(goods.getId());
        // goodsImage
        if (StringUtils.isNotBlank(goodsDetail.getImages())) {
            goodsSVBo.setGoodsImage(goodsDetail.getImages().split(",")[0]);
        }
        // goodsTitle
        goodsSVBo.setGoodsTitle(goods.getTitle());
        // goodsPrice
        goodsSVBo.setGoodsPrice(goodsDetail.getPrice());
        // createTime
        goodsSVBo.setCreateTime(goods.getCreateTime());
        return goodsSVBo;
    }
}
