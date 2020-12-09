package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @description:OrderMapper
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
public interface OrderMapper extends Mapper<Order>, SelectByIdListMapper<Order, Long> {

    /**
     * 统计指定年份 1-12月 的销售额
     *
     * @param year
     * @return
     */
    @Select("select * from v_saleroom where create_year = #{year} ORDER BY create_month")
    List<Map<String, Object>> statisticSaleroom(@Param("year") int year);

    /**
     * 统计指定年份，商品销售量top10
     *
     * @param year
     * @return
     */
    @Select("select * from v_goods_salesvolume where `year` = #{year} ORDER BY sales_volume desc LIMIT 10")
    List<Map<String, Object>> statisticSalesvolume(@Param("year") int year);

    /**
     * 统计指定年份 1-12月 的用户注册量
     *
     * @param year
     * @return
     */
    @Select("select * from v_regist_num WHERE regist_year = #{year}  ORDER BY regist_month")
    List<Map<String, Object>> statisticRegistNum(@Param("year") int year);

}
