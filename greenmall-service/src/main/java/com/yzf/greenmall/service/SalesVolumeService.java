package com.yzf.greenmall.service;

import com.yzf.greenmall.entity.SalesVolume;
import com.yzf.greenmall.mapper.SalesVolumeMapper;
import com.yzf.greenmall.mapper.SalesVolumeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.util.Calendar;

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

}
