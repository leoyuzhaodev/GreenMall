package com.yzf.greenmall.service;


import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.NumberCalUtil;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.Order;
import com.yzf.greenmall.entity.OrderDetail;
import com.yzf.greenmall.entity.SubmitOrderBo;
import com.yzf.greenmall.mapper.GoodsMapper;
import com.yzf.greenmall.mapper.OrderDetailMapper;
import com.yzf.greenmall.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

/**
 * @description:OrderService
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 提交订单
     *
     * @param loginUser
     * @param list
     * @return
     */
    public Message submitOrder(UserInfo loginUser, List<SubmitOrderBo> list) {

        // 1，数据验证
        if (CollectionUtils.isEmpty(list) || loginUser == null) {
            return new Message(2, "订单异常，无法创建订单。");
        }

        // 2，计算总金额
        Double totalPrice = 0D;
        for (SubmitOrderBo item : list) {
            totalPrice = NumberCalUtil.add(totalPrice,
                    NumberCalUtil.multiply(item.getUnitPrice(), item.getNum().doubleValue()));
            // 检查商品是否失效
            boolean flag = goodsService.isGoodsAvailable(item.getGoodsId(), item.getNum());
            if (flag == false) {
                return new Message(2, "订单中存在商品，已经失效!");
            }
        }

        // 3，生成订单数据，并存入数据库
        Order order = new Order();
        order.setAccountId(loginUser.getId());
        order.setAddressId(list.get(0).getAddressId());
        order.setCreateTime(new Date());
        order.setState(Order.STATE_PAYED_SEND);
        order.setTotalPrice(totalPrice);
        order.setValid(Order.VALID_YES);
        int i = orderMapper.insertSelective(order);
        if (i <= 0) {
            return new Message(2, "订单创建失败!");
        }

        // 4，生成订单详情数据，并存入数据库
        for (SubmitOrderBo item : list) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getId());
            orderDetail.setGoodsId(item.getGoodsId());
            orderDetail.setUnitPrice(item.getUnitPrice());
            orderDetail.setNum(item.getNum());
            orderDetail.setState(OrderDetail.STATE_NORMAL);
            int i1 = orderDetailMapper.insertSelective(orderDetail);
            if (i1 <= 0) {
                return new Message(2, "订单创建失败!");
            }
        }

        // 5，扣账户余额
        boolean flag = userService.cost(loginUser.getId(), totalPrice);
        if (!flag) {
            return new Message(2, "您的账户余额不足了。");
        }

        // 订单提交成功
        return new Message(1, "");
    }
}
