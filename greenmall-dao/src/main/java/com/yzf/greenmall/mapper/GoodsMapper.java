package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Goods;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:GoodsMapper
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
public interface GoodsMapper extends Mapper<Goods> {

    @Select("select * from tb_goods where saleable = 1 and valid = 1")
    @Results(id = "GoodsResultMap", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "sub_title", property = "subTitle"),
            @Result(column = "cid1", property = "cid1"),
            @Result(column = "cid2", property = "cid2"),
            @Result(column = "cid3", property = "cid3"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "sales_volume", property = "salesVolume"),
            @Result(column = "id", property = "goodsDetail", one = @One(select = "com.yzf.greenmall.mapper.GoodsDetailMapper.findGoodsDetailById", fetchType = FetchType.EAGER))
    })
    List<Goods> findAllGoodsAndDetail();

    @Select("select * from tb_goods where id = #{goodsId} and saleable = 1 and valid = 1")
    @ResultMap(value = "GoodsResultMap")
    Goods findGoodsAndDetail(@Param("goodsId") Long goodsId);

    /**
     * 查询销量前 topNumebr 的商品ID
     *
     * @param topNumebr
     * @return
     */
    @Select("select goods_id from tb_sales_volume group by goods_id order by sum(sales_volume) desc limit #{topNumebr}")
    List<Long> findSaleVolumeTopGoods(@Param("topNumebr") Integer topNumebr);

}
