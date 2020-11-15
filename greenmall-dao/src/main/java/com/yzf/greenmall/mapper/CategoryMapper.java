package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:CategoryMapper
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {

    @Select("select name from tb_category where id in (select category_id from tb_params_category where param_id = #{pId})")
    List<String> findCategoryNamesByPramsId(@Param("pId") Long pId);
}
