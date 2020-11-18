package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Param;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 根据规格参数id查找关联的所有分类的id
     *
     * @param paramId
     * @return
     */
    @Select("select category_id from tb_params_category where param_id = #{pId}")
    List<Long> findCIdsByPId(@org.apache.ibatis.annotations.Param("pId") Long paramId);

    /**
     * 关联规格参数和分类
     *
     * @param pId
     * @param cId
     */
    @Insert("insert into tb_params_category(param_id,category_id) values(#{pId},#{cId})")
    void insertParamCategory(@org.apache.ibatis.annotations.Param("pId") Long pId,
                             @org.apache.ibatis.annotations.Param("cId") Long cId);

    /**
     * 解除规格参数和分类的关联
     *
     * @param pId
     * @param cId
     */
    @Insert("delete from tb_params_category where param_id=#{pId} and category_id=#{cId}")
    void deleteParamCategory(@org.apache.ibatis.annotations.Param("pId") Long pId,
                             @org.apache.ibatis.annotations.Param("cId") Long cId);


    /**
     * 根据 规格参数id,类型id查询：是否有商品关联该规格参数
     *
     * @param key
     * @param cId
     * @return
     */
    @Select("select count(*) from tb_goods_detail where goods_id in (select id from tb_goods where cid3 = #{cId}) and params like #{key}")
    Long findGoodsCountByPIdAndCId(@org.apache.ibatis.annotations.Param("key") String key,
                                   @org.apache.ibatis.annotations.Param("cId") Long cId);

    /**
     * 根据 规格参数id查询：是否有商品关联该规格参数
     *
     * @param key
     * @return
     */
    @Select("select count(*) from tb_goods_detail where params like #{key}")
    Long findGoodsCountByPId(@org.apache.ibatis.annotations.Param("key") String key);
}
