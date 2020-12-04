package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.OrderDetail;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:OrderDetailMapper
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
public interface OrderDetailMapper extends Mapper<OrderDetail>, SelectByIdListMapper<OrderDetail, Long> {
}
