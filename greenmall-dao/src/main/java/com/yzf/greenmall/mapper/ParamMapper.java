package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:ParamMapper
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
public interface ParamMapper extends Mapper<Param> {

    /**
     * 根据【三级分类】查找规格参数
     *
     * @param cid
     * @return
     */
    @Select("select * from tb_param where id in (select param_id from tb_params_category where category_id = #{cid})")
    List<Param> findParamsByCid(@org.apache.ibatis.annotations.Param("cid") Long cid);

    /**
     * 根据【三级分类】查找规格参数的ID
     *
     * @param cid
     * @return
     */
    @Select("select param_id from tb_params_category where category_id = #{cid}")
    List<Long> findParamsIdByCid(@org.apache.ibatis.annotations.Param("cid") Long cid);

}
