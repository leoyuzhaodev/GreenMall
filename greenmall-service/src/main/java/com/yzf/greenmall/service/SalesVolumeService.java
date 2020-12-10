package com.yzf.greenmall.service;

import com.yzf.greenmall.common.DateUtils;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.entity.OrderDetail;
import com.yzf.greenmall.entity.SalesVolume;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.OrderDetailMapper;
import com.yzf.greenmall.mapper.SalesVolumeMapper;
import com.yzf.greenmall.mapper.SalesVolumeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.Id;
import java.util.Calendar;
import java.util.List;

/**
 * @description:SalesVolumeService
 * @author:leo_yuzhao
 * @date:2020/11/19
 */
@Transactional
@Service
public class SalesVolumeService {

    @Autowired
    private SalesVolumeMapper salesVolumeMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    @Autowired
    private GoodsService goodsService;

    /**
     * 添加销量数据
     *
     * @param salesVolume
     */
    public void add(SalesVolume salesVolume) {
        // year month goodsId salesVolume
        salesVolumeMapper.insertSelective(salesVolume);
    }

    /**
     * 获取指定商品的总销量
     *
     * @param goodsId
     * @return
     */
    public Long getGoodsAllSalesVolume(Long goodsId) {
        Long salesVolume = salesVolumeMapper.findGoodsAllSalesVolume(goodsId);
        return salesVolume == null ? 0 : salesVolume;
    }


    /**
     * 获取指定商品，指定年份，指定月份的销量
     *
     * @param year
     * @param month
     * @param goodsId
     * @return
     */
    public Long getGoodsSVByMonthAndYear(Integer year, Integer month, Long goodsId) {
        SalesVolume record = new SalesVolume(year, month, goodsId);
        SalesVolume salesVolume = salesVolumeMapper.selectOne(record);
        if (salesVolume == null) {
            return 0l;
        } else {
            return salesVolume.getSalesVolume();
        }
    }

    /**
     * 根据订单ID更新销量
     *
     * @param orderId
     */
    public void updateSalesVolume(Long orderId) {
        // 0，验证数据
        if (orderId == null) {
            throw new RuntimeException("根据订单ID更新销量：订单ID为空");
        }

        // 1，根据订单Id 查询订单详情
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(record);
        if (CollectionUtils.isEmpty(orderDetails)) {
            throw new RuntimeException("根据订单ID更新库存：根据订单ID无法查询到订单详情");
        }

        // 2，根据订单详情更新销量
        for (OrderDetail item : orderDetails) {
            updateSalesVolume(item.getGoodsId(), new Long(item.getNum()));
        }
    }

    /**
     * 根据商品id 和 销量更新销量表
     *
     * @param goodsId
     * @param num
     */
    public void updateSalesVolume(Long goodsId, Long num) {

        SalesVolume record = new SalesVolume();
        record.setYear(DateUtils.getCurYear());
        record.setMonth(DateUtils.getCurMonth());
        record.setGoodsId(goodsId);

        SalesVolume salesVolume = salesVolumeMapper.selectOne(record);
        if (salesVolume != null) {
            // 更新销量
            salesVolume.setSalesVolume(num + salesVolume.getSalesVolume());
            salesVolumeMapper.updateByPrimaryKeySelective(salesVolume);
        } else {
            // 新增销量
            record.setSalesVolume(num);
            salesVolumeMapper.insertSelective(record);
        }

        // 更新索引库
        goodsService.updateGoodsSearch(goodsId);

    }


}
