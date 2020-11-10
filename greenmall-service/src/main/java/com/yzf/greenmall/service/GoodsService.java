package com.yzf.greenmall.service;

import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:GoodsService
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Service
@Transactional
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsService.class);

    /**
     * 添加商品
     *
     * @param goodsBo
     * @return
     */
    public void add(GoodsBo goodsBo) {
        // 1，初始化商品相关参数
        Goods goods = goodsBo.generateGoods();
        // 2，保存商品
        // 当 i>0 表明数据已经存入数据库
        int i = goodsMapper.insertSelective(goods);
        if (i <= 0) {
            throw new RuntimeException("Goods数据保存失败");
        }
        // 3，保存商品详情
        goodsBo.setGoodsId(goods.getId());
        // 当 i>0 表明数据已经存入数据库
        goodsBo.setGoodsId(goods.getId());
        int j = goodsDetailMapper.insertSelective(goodsBo);
        if (j <= 0) {
            throw new RuntimeException("GoodsDetail数据保存失败");
        }
    }
}
