package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Order;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:OrderMapper
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
public interface OrderMapper extends Mapper<Order>, SelectByIdListMapper<Order, Long> {
}
