package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Goods;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
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
    @Results({
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

}
