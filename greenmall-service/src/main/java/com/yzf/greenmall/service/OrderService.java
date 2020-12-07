package com.yzf.greenmall.service;


import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.NumberCalUtil;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.*;
import com.yzf.greenmall.mapper.*;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    @Autowired
    private EvaluateService evaluateService;

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

    /**
     * 分页查询订单数据
     *
     * @param queryPage
     * @return
     */
    public LayuiPage<Order> findOrderByPage(QueryPage<Order> queryPage) {

        if (CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            queryPage.setQueryMap(Order.originalQueryMap());
        }
        Example example = queryPage.generateExample(Order.class, false);
        // 2，分页查询
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        List<Order> orderList = orderMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(orderList)) {
            return new LayuiPage<Order>();
        }

        // 3，封装分页信息，并返回
        return new LayuiPage<Order>().initLayuiPage(orderList);

    }

    /**
     * 查询订单详情
     *
     * @param orderId
     * @return
     */
    public List<OrderDetail> findOrderDetail(Long orderId) {
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        List<OrderDetail> list = orderDetailMapper.select(record);
        if (!CollectionUtils.isEmpty(list)) {
            // 加载商品名称
            list.forEach(orderDetail -> {
                Goods goods = goodsMapper.selectByPrimaryKey(orderDetail.getGoodsId());
                orderDetail.setGoodsTitle(goods.getTitle());
            });
        }
        return list;
    }

    /**
     * 为订单设置物流单号
     * {"orderId":"7","logisticsId":"1","logisticsFlag":"STO"}
     *
     * @param logisticsInfo
     * @return
     */
    public Message setLogisticsInfo(Map<String, String> logisticsInfo) {
        // 1，获取订单和物流信息
        String orderId = logisticsInfo.get("orderId");
        String logisticsId = logisticsInfo.get("logisticsId");
        String logisticsFlag = logisticsInfo.get("logisticsFlag");
        if (StringUtils.isEmpty(orderId) ||
                StringUtils.isEmpty(logisticsId) ||
                StringUtils.isEmpty(logisticsFlag)) {
            throw new RuntimeException("为订单设置物流单号，缺少必要的信息");
        }

        // 2，设置物流单号，更新订单信息，改变订单状态
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单号不存在本系统");
        }
        order.setLogisticsId(logisticsId);
        order.setLogisticsFlag(logisticsFlag);
        order.setState(Order.STATE_SENT_SIGN);
        orderMapper.updateByPrimaryKeySelective(order);

        return new Message(1, "");
    }

    /**
     * 查询登录用户的所有订单
     *
     * @param loginUser
     * @return
     */
    public List<Order> findOrder(UserInfo loginUser) {

        // 1，查找所有的订单
        Order record = new Order();
        record.setAccountId(loginUser.getId());
        record.setValid(Order.VALID_YES); // 用户未删除
        List<Order> orderList = orderMapper.select(record);
        if (CollectionUtils.isEmpty(orderList)) {
            return null;
        }

        // 2，封装订单数据
        orderList.forEach(order -> {
            // 1，检测订单是否评价
            order.setIsNeedEvaluate(Order.EVALUATE_NO);
            if (order.getState() == Order.STATE_FINISHED) {
                // 订单完成后，但是没有评价，此时需要评价标识为：1
                if (!evaluateService.isOrderEvaluated(order.getId())) {
                    order.setIsNeedEvaluate(Order.EVALUATE_YES);
                }
            }
            // 2，查询订单详情 & 设置订单详情
            order.setOrderDetailList(findOrderDetails(order.getId()));
        });

        return orderList;
    }

    /**
     * 根据订单id查询订单详情
     *
     * @param orderId
     * @return
     */
    public List<OrderDetail> findOrderDetails(Long orderId) {
        // 1,查询订单详情
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        List<OrderDetail> list = orderDetailMapper.select(record);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("根据订单id" + orderId + "没有查询到订单详情");
        }

        // 2,封装数据
        list.forEach(item -> {
            // 1，设置商品名称和图片
            Goods goods = goodsMapper.selectByPrimaryKey(item.getGoodsId());
            GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(item.getGoodsId());
            item.setGoodsTitle(goods.getTitle());
            item.setGoodsImage(goodsDetail.getImages().split(",")[0]);
        });

        return list;
    }

}
