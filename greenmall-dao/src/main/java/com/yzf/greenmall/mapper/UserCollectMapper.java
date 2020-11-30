package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.UserCollect;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:UserCollectMapper
 * @author:leo_yuzhao
 * @date:2020/11/30
 */
public interface UserCollectMapper extends Mapper<UserCollect>,
        SelectByIdListMapper<UserCollect, Long> {
}
