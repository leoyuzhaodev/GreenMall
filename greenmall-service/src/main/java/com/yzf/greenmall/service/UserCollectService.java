package com.yzf.greenmall.service;

import com.yzf.greenmall.bo.UserCollectBo;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.entity.UserCollect;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import com.yzf.greenmall.mapper.SalesVolumeMapper;
import com.yzf.greenmall.mapper.UserCollectMapper;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
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

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private SalesVolumeService salesVolumeService;

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
     *
     * @param loginUser
     * @param goodsIds
     * @return
     */
    public Message addBatch(UserInfo loginUser, List<Long> goodsIds) {
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

    /**
     * 加载收藏夹数据
     *
     * @param loginUser
     * @param loadNum
     * @return
     */
    public List<UserCollectBo> findCollect(UserInfo loginUser, Long loadNum) {

        // 1，加载数据
        UserCollect record = new UserCollect();
        record.setAccountId(loginUser.getId());
        List<UserCollect> userCollects = userCollectMapper.selectByRowBounds(record, new RowBounds(0, loadNum.intValue()));

        // 2，组装数据
        if (CollectionUtils.isEmpty(userCollects)) {
            return null;
        }
        List<UserCollectBo> lists = new ArrayList<>();
        userCollects.forEach(userCollect -> {
            UserCollectBo userCollectBo = new UserCollectBo();
            // id
            userCollectBo.setId(userCollect.getId());
            // goodsId,查找商品信息
            Goods goods = goodsMapper.selectByPrimaryKey(userCollect.getGoodsId());
            if (goods == null) {
                throw new RuntimeException("需要查询的商品不存在。");
            } else {
                goods.setGoodsDetail(goodsDetailMapper.selectByPrimaryKey(goods.getId()));
            }
            userCollectBo.setGoodsId(goods.getId());
            // image
            userCollectBo.setImage(goods.getGoodsDetail().getImages().split(",")[0]);
            // title
            userCollectBo.setTitle(goods.getTitle());
            // subTitle
            userCollectBo.setSubTitle(goods.getSubTitle());
            // price
            userCollectBo.setPrice(goods.getGoodsDetail().getPrice());
            // goodEvaluateDegree
            userCollectBo.setGoodEvaluateDegree(evaluateService.findGoodsEvaluateDegree(goods.getId()));
            // saleMonth
            Calendar calendar = Calendar.getInstance();
            Long monthSalesVolume = salesVolumeService.getGoodsSVByMonthAndYear(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    goods.getId());
            userCollectBo.setSaleMonth(monthSalesVolume);
            // valid
            if (goods.getSaleable() == false || goods.getValid() == false) {
                userCollectBo.setValid(0);
            } else {
                userCollectBo.setValid(1);
            }
            lists.add(userCollectBo);
        });

        return lists;
    }

    /**
     * 删除收藏
     *
     * @param loginUser
     * @param collectId
     */
    public void deleteCollection(UserInfo loginUser, Long collectId) {
        UserCollect record = new UserCollect();
        record.setId(collectId);
        userCollectMapper.delete(record);
    }
}
