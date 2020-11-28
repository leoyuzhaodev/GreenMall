package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Evaluate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:EvaluateMapper
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
public interface EvaluateMapper extends Mapper<Evaluate>, SelectByIdListMapper<Evaluate, Long> {
    /**
     * 统计该商品的所有评价
     *
     * @param id 商品id
     * @return
     */
    @Select("SELECT count(*) FROM `tb_evaluate` where goods_id = #{id}")
    Long getTotalEvaluate(@Param("id") Long id);

    /**
     * 统计中评数量
     *
     * @param id
     * @return
     */
    @Select("SELECT count(*) FROM `tb_evaluate` where goods_id = #{id} and score = 3")
    Long getCommonEvaluateNum(@Param("id") Long id);

    /**
     * 统计好评数量
     *
     * @param id
     * @return
     */
    @Select("SELECT count(*) FROM `tb_evaluate` where goods_id = #{id} and score > 3")
    Long getGoodEvaluateNum(@Param("id") Long id);


    /**
     * 统计差评数量
     *
     * @param id
     * @return
     */
    @Select("SELECT count(*) FROM `tb_evaluate` where goods_id = #{id} and score < 3")
    Long getBadEvaluateNum(@Param("id") Long id);
}
