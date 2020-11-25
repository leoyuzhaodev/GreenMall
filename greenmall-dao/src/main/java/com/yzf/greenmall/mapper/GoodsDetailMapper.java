package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:GoodsDetailMapper
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
public interface GoodsDetailMapper extends Mapper<GoodsDetail> {

    /**
     * 根据商品id查询商品详情
     *
     * @param id
     * @return
     */
    @Select("select * from tb_goods_detail where goods_id = #{id}")
    GoodsDetail findGoodsDetailById(@Param("id") Long id);

    /**
     * 根据商品id查询商品库存
     *
     * @param goodsId
     * @return
     */
    @Select("select stock from tb_goods_detail where goods_id = #{goodsId};")
    Long findStockByGoodsId(@Param("goodsId") Long goodsId);
}
