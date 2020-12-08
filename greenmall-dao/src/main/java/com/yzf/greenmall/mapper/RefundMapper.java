package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.Refund;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:退款Mapper
 * @author:leo_yuzhao
 * @date:2020/12/8
 */
public interface RefundMapper extends Mapper<Refund>, SelectByIdListMapper<Refund, Long> {
}
