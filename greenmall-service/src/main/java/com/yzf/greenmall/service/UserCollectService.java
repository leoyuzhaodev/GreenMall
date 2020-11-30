package com.yzf.greenmall.service;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.UserCollect;
import com.yzf.greenmall.mapper.GoodsMapper;
import com.yzf.greenmall.mapper.UserCollectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @description:UserCollectService
 * @author:leo_yuzhao
 * @date:2020/11/30
 */
@Service
@Transactional
public class UserCollectService {
    @Autowired
    private UserCollectMapper userCollectMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopCartService shopCartService;

    static final Logger LOGGER = LoggerFactory.getLogger(ShopCartService.class);


    /**
     * 新增收藏
     *
     * @param loginUser
     * @param goodsId
     * @return
     */
    public Message add(UserInfo loginUser, Long goodsId) {
        try {
            // 1，判断商品是否存在
            int i = goodsMapper.selectCount(new Goods(goodsId));
            if (i < 0) {
                return new Message(2, "收藏的商品不存在");
            }
            // 2，收藏商品
            UserCollect userCollect = new UserCollect(new Date(), goodsId, loginUser.getId());
            userCollectMapper.insertSelective(userCollect);
            // 3，删除购物车数据
            shopCartService.delete(loginUser, goodsId);
            return new Message(1, "");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("新增收藏异常：{}", e.getMessage());
            return new Message(2, "服务器异常！");
        }
    }

    /**
     * 批量新增收藏
     * @param loginUser
     * @param goodsIds
     * @return
     */
    public Message addBatch(UserInfo loginUser, List<Long> goodsIds){
        try {
            goodsIds.forEach(goodsId -> {
                this.add(loginUser, goodsId);
            });
            return new Message(1, "");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("批量新增收藏异常：{}", e.getMessage());
            return new Message(2, "服务器异常！");
        }
    }

}
