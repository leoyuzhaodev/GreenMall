package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.AdministrativeRegion;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:行政区划Mapper
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
public interface AdministrativeRegionMapper extends Mapper<AdministrativeRegion>,
        SelectByIdListMapper<AdministrativeRegion, Long> {
}
