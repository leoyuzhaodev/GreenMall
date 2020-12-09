package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.SalesVolume;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:SalesVolumeMapper
 * @author:leo_yuzhao
 * @date:2020/11/19
 */
public interface SalesVolumeMapper extends Mapper<SalesVolume> {
    /**
     * 查询指定商品的总销量
     *
     * @param goodsId
     * @return
     */
    @Select("select sum(sales_volume) from tb_sales_volume where goods_id = #{goodsId}")
    Long findGoodsAllSalesVolume(@Param("goodsId") Long goodsId);




}
