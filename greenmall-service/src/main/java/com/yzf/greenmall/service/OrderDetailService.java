package com.yzf.greenmall.service;

import com.yzf.greenmall.mapper.OrderDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:OrderDetailService
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Service
@Transactional
public class OrderDetailService {
    @Autowired
    private OrderDetailMapper orderDetailMapper;
}
