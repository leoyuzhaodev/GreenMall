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

    @Select("select * from tb_goods_detail where goods_id = #{id}")
    GoodsDetail findGoodsDetailById(@Param("id") Long id);

}
