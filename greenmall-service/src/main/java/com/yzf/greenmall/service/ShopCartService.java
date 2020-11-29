package com.yzf.greenmall.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yzf.greenmall.bo.ShopCartBo;
import com.yzf.greenmall.common.JsonUtils;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.entity.ShopCart;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:购物车服务
 * @author:leo_yuzhao
 * @date:2020/11/28
 */
@Service
@Transactional
public class ShopCartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    static final String KEY_PREFIX = "leyou:cart:uid:";

    static final Logger LOGGER = LoggerFactory.getLogger(ShopCartService.class);

    /**
     * 加购物车
     * Map<userId,Map<goodsId,商品信息>>
     *
     * @param shopCart
     * @return
     */
    public Message add(ShopCart shopCart, UserInfo userInfo, boolean isUpdate) {
        try {
            // 获取用户id
            Long userId = userInfo.getId();
            shopCart.setAccountId(userId);
            // 根据 userId 生成最外层的key
            String key = KEY_PREFIX + userId;
            // 获取 hash 操作对象
            BoundHashOperations<String, Object, Object> hashOperations = this.stringRedisTemplate.boundHashOps(key);
            // 判断redis中是否已经存在该商品信息
            Long goodsId = shopCart.getGoodsId();
            Long num = shopCart.getNum();
            Boolean flag = hashOperations.hasKey(goodsId.toString());
            if (flag) {
                // 存在：修改数量
                // 1，获取购物车数据
                String oldCartJson = hashOperations.get(goodsId.toString()).toString();
                // 2，将json数据转化为对象
                ShopCart oldCart = JsonUtils.parse(oldCartJson, ShopCart.class);
                // 3，更新商品数量
                if (isUpdate) {
                    oldCart.setNum(num);
                } else {
                    oldCart.setNum(oldCart.getNum() + num);
                }
                shopCart = oldCart;
            }
            // 检查商品是否失效
            if (!goodsService.isGoodsAvailable(shopCart.getGoodsId())) {
                return new Message(4, "该商品已经失效。");
            }
            // 检查库存是否充足 -1 不足：具体的库存数量
            Long stock = isStockEnough(shopCart.getGoodsId(), shopCart.getNum());
            if (-1l != stock) {
                return new Message(3, "" + stock);
            }
            // 将购物车数据转化为 JSON 字符串，存入redis数据库
            hashOperations.put(goodsId.toString(), JsonUtils.serialize(shopCart));
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(2, "服务器内部错误。");
        }
        return new Message(1, "");
    }

    /**
     * 判断商品库存是否充足
     *
     * @param goodsId
     * @param num
     * @return 充足：-1 不足：具体库存数量
     */
    private Long isStockEnough(Long goodsId, Long num) {
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(goodsId);
        if (goodsDetail.getStock() - num < 0) {
            return goodsDetail.getStock();
        }
        return -1l;
    }

    /**
     * 购物车展示
     *
     * @param loginUser
     * @return
     */
    public List<ShopCartBo> findCart(UserInfo loginUser) {

        // 1，生成key
        String key = KEY_PREFIX + loginUser.getId();
        if (!this.stringRedisTemplate.hasKey(key)) {
            return null;
        }

        // 2，根据key获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        List<Object> values = hashOps.values();
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }

        // 3，遍历购物车数据（JSON字符串）生成 List<ShopCartBo>
        List<ShopCartBo> shopCartBos = new ArrayList<>();
        values.forEach(val -> {
            ShopCart shopCart = JsonUtils.parse(val.toString(), ShopCart.class);
            // 1，根据商品id查询商品,查询商品详情
            Goods goods = goodsMapper.selectByPrimaryKey(shopCart.getGoodsId());
            goods.setGoodsDetail(goodsDetailMapper.selectByPrimaryKey(shopCart.getGoodsId()));
            if (goods == null) {
                throw new RuntimeException("查询购物车数据：商品id为：" + shopCart.getGoodsId() + "的商品数据不存在!");
            }
            // 2，封装数据
            ShopCartBo shopCartBo = new ShopCartBo();
            shopCartBo.setGoodsId(goods.getId());
            shopCartBo.setGoodsImage(goods.getGoodsDetail().getImages().split(",")[0]);
            shopCartBo.setGoodsTitle(goods.getTitle());
            shopCartBo.setPrice(goods.getGoodsDetail().getPrice());
            shopCartBo.setAvailable(goodsService.isGoodsAvailable(goods.getId()) || goods.getGoodsDetail().getStock() == 0);
            // 处理商品库存不足问题
            shopCartBo.setNum(shopCart.getNum());
            if (shopCartBo.getAvailable()) {
                if (goods.getGoodsDetail().getStock() - shopCart.getNum() < 0) {
                    shopCartBo.setNum(1l);
                    // 更新数据库
                    add(new ShopCart(goods.getId(), shopCartBo.getNum()), loginUser, true);
                }
            }
            shopCartBo.setStock(goods.getGoodsDetail().getStock());
            shopCartBo.setGoodsSubTitle(goods.getSubTitle());
            shopCartBos.add(shopCartBo);
        });
        return shopCartBos;
    }

    /**
     * 删除购物车数据
     *
     * @param loginUser
     * @param goodsId
     * @return
     */
    public Message delete(UserInfo loginUser, Long goodsId) {
        try {
            // 1，根据用户拼接key
            String key = KEY_PREFIX + loginUser.getId();
            // 2，获取 hash 操作对象
            BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
            Boolean flag = hashOps.hasKey(goodsId.toString());
            if (flag) {
                // 3，执行删除
                hashOps.delete(goodsId.toString());
                return new Message(1, "");
            } else {
                return new Message(2, "需要删除的购物车数据不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("删除购物车数据异常：{}", e.getMessage());
            return new Message(2, "服务器异常！");
        }
    }
}
