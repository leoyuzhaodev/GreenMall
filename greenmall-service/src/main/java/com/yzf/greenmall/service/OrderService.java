package com.yzf.greenmall.service;


import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.NumberCalUtil;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.*;
import com.yzf.greenmall.mapper.*;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("accountId", loginUser.getId());
        criteria.andEqualTo("valid", Order.VALID_YES);
        example.setOrderByClause("create_time desc"); // 按照创建时间降序查找

        List<Order> orderList = orderMapper.selectByExample(example);
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

    /**
     * 确认收货
     *
     * @param orderId
     * @return
     */
    public Message confirmReceipt(Long orderId) {
        Order order = this.orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return new Message(2, "订单编号为：" + orderId + " 的订单不存在！");
        }
        order.setState(Order.STATE_FINISHED);
        orderMapper.updateByPrimaryKeySelective(order);
        return new Message(1, "收货成功");
    }

    /**
     * 根据订单ID删除订单
     *
     * @param orderId
     * @return
     */
    public Message deleteOrder(Long orderId) {

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            new RuntimeException("需要删除的订单不存在");
        }

        if (!(order.getState() == Order.STATE_FINISHED || order.getState() == Order.STATE_CLOSED)) {
            return new Message(2, "没有交易完成，或者交易关闭的订单不能删除!");
        }
        order.setValid(Order.EVALUATE_NO);
        orderMapper.updateByPrimaryKeySelective(order);

        return new Message(1, "");
    }

    /**
     * 退款：更改订单中产品的状态，如果订单中的商品全部退款，交易将会关闭
     *
     * @param orderId
     * @param goodsId
     */
    public void refundGoods(Long orderId, Long goodsId) {
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        record.setGoodsId(goodsId);
        OrderDetail orderDetail = orderDetailMapper.selectOne(record);
        if (orderDetail.getState() != OrderDetail.STATE_NORMAL) {
            throw new RuntimeException("退款：更改订单中产品的状态，该产品已退款，无法进行二次退款!");
        }
        if (orderDetail == null) {
            throw new RuntimeException("退款：更改订单中产品的状态，根据订单ID,产品ID无法查询到订单详情!");
        }

        // 更新订单详情状态
        orderDetail.setState(OrderDetail.STATE_REFUNDED);
        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);

        // 判断交易是否关闭
        // 1，查询订单详情正常的条目数，如果正常的条目数为 0 则表示交易关闭
        record = new OrderDetail();
        record.setOrderId(orderId);
        record.setState(OrderDetail.STATE_NORMAL);
        int count = orderDetailMapper.selectCount(record);
        if (count == 0) {
            // 更新订单状态
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                throw new RuntimeException("退款：更改订单状态，根据订单ID无法查询到订单!");
            }
            order.setState(Order.STATE_CLOSED);
            // 更新数据库中的订单状态
            orderMapper.updateByPrimaryKeySelective(order);
        }

    }

    /**
     * 获取订单中的某件商品的退款状态
     *
     * @param orderId
     * @param goodsId
     * @return
     */
    public Byte findRefundState(Long orderId, Long goodsId) {
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        record.setGoodsId(goodsId);
        OrderDetail orderDetail = orderDetailMapper.selectOne(record);
        if (orderDetail == null) {
            throw new RuntimeException("获取订单中的某件商品的退款状态异常：根据商品ID和订单ID无法查找到订单详情！");
        }
        return orderDetail.getState().byteValue();
    }

    /**
     * 统计指定年份 1-12月 的销售额
     *
     * @param year
     * @return
     */
    public List<Map<String, Object>> statisticSaleroom(Integer year) {

        // 1，获取当前年份
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        List<Integer> yearList = new ArrayList<>();

        // 2，组装需要查询的年份
        if (year == null || year > curYear) {
            year = curYear;
        }
        for (int i = 1; i <= 3; i++) {
            if (year >= curYear) {
                yearList.add(year - 3 + i);
            } else {
                yearList.add(year - 2 + i);
            }
        }
        // 3，根据年份进行后台查询，并组装前台数据
        List<Map<String, Object>> listInfo = new ArrayList<>();
        yearList.forEach(qYear -> {
            Map<String, Object> mapInfo = new HashMap<>();
            List<Map<String, Object>> list = orderMapper.statisticSaleroom(qYear);
            mapInfo.put("year", qYear);
            mapInfo.put("saleroomInfo", list);
            listInfo.add(mapInfo);
        });

        return listInfo;
    }

    /**
     * 统计指定年份，商品销售量top10
     *
     * @param year
     * @return
     */
    public Map<String, Object> statisticSalesvolume(Integer year) {
        // 年份获取
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        if (year == null || year > curYear) {
            year = curYear;
        }
        // 查询数据
        List<Map<String, Object>> maps = orderMapper.statisticSalesvolume(year);
        // 组装商品名称到map中
        if (!CollectionUtils.isEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                Long goods_id = (Long) map.get("goods_id");
                Goods goods = goodsMapper.selectByPrimaryKey(goods_id);
                map.put("goods_name", goods.getTitle());
            }
        }
        // 组装返回数据
        Map<String, Object> info = new HashMap<>();
        info.put("year", year);
        info.put("salesvolumeInfo", maps);
        return info;
    }

    /**
     * 统计指定年份 1-12月 的用户注册量
     *
     * @param year
     * @return
     */
    public Map<String, Object> statisticRegistNum(Integer year) {
        // 年份获取
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        if (year == null || year > curYear) {
            year = curYear;
        }
        // 查询数据
        List<Map<String, Object>> maps = orderMapper.statisticRegistNum(year);
        // 组装返回数据
        Map<String, Object> info = new HashMap<>();
        info.put("year", year);
        info.put("registNumInfo", maps);
        return info;
    }

}
