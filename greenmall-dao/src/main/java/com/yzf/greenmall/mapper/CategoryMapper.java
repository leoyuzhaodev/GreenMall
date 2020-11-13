package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:CategoryMapper
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {
}
