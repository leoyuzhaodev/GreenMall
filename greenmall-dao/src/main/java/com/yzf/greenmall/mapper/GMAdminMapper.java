package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.GMAdmin;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:GMAdminMapper
 * @author:leo_yuzhao
 * @date:2020/11/22
 */
public interface GMAdminMapper extends Mapper<GMAdmin>, SelectByIdListMapper<GMAdmin, Long> {

}
