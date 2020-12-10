package com.yzf.greenmall.service;

import com.yzf.greenmall.bo.GoodsSearch;
import com.yzf.greenmall.common.CodecUtils;
import com.yzf.greenmall.common.NumberCalUtil;
import com.yzf.greenmall.common.NumberUtils;
import com.yzf.greenmall.entity.*;
import com.yzf.greenmall.mapper.*;
import com.yzf.greenmall.repository.GoodsRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description: 测试
 * @author:leo_yuzhao
 * @date:2020/12/9
 */
@Service
@Transactional
public class TestService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 初始化测试用户
     */
    public void initUserInfo() {

        for (int i = 1; i < 1001; i++) {
            User user = new User();
            user.setName("测试name" + i);
            user.setNickName("测试nickName" + i);
            user.setSex(i % 3);
            user.setBirthday(new Date());
            user.setPhone(NumberUtils.generateCode(11));
            user.setEmail("test" + i + "@qq.com");
            // 随机创建时间
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(NumberUtils.generateNumber(2018, 2020),
                    NumberUtils.generateNumber(1, 12) - 1,
                    NumberUtils.generateNumber(0, 28));
            Date time = calendar.getTime();
            user.setCreateDate(time);
            // 设置头像
            user.setPortrait("http://image.greenmall.com/group1/M00/00/01/wKjmgF_GBcKAQrS2AABiHYHHMos674.jpg");
            // 设置密码
            String pwd = "123456";
            String salt = CodecUtils.generateSalt();
            user.setPassword(CodecUtils.md5Hex(pwd, salt));
            user.setSalt(salt);
            // 设置财产
            user.setPossession(1000D);
            // 设置禁用
            user.setValid(User.USER_VALID_NO);
            // 设置状态
            user.setState(User.USER_STATE_NOT_INIT);
            userMapper.insertSelective(user);
            System.out.println("新增测试用户：" + i);
        }
    }

    /**
     * 测试统计方法
     */
    public void test1() {
        List<Map<String, Object>> maps = orderMapper.statisticRegistNum(2020);
        List<Map<String, Object>> maps1 = orderMapper.statisticSaleroom(2020);
        List<Map<String, Object>> maps2 = orderMapper.statisticSalesvolume(2020);
        System.out.println();
    }

    public void initOrderInfo() {
        for (int i = 0; i < 500; i++) {
            randomOneOrder();
            System.out.println("成功：" + (i + 1));
        }
    }

    private void randomOneOrder() {

        // 1，商品id 30 - 1000
        Map<Long, Integer> goodsInfo = new HashMap<>();
        // 2，随机订单详情个数
        int odNumber = NumberUtils.generateNumber(1, 3);
        for (int i = 1; i <= odNumber; i++) {
            goodsInfo.put(new Long(NumberUtils.generateNumber(30, 1000)), NumberUtils.generateNumber(1, 5));
        }
        // 3，生成商品详情并统计商品个数
        List<OrderDetail> orderDetails = new ArrayList<>();
        Set<Long> ids = goodsInfo.keySet();
        Double totalPrice = 0D;
        for (Long goodsId : ids) {
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            if (goods != null) {
                OrderDetail orderDetail = new OrderDetail();
                GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(goodsId);
                orderDetail.setGoodsId(goodsId);
                orderDetail.setUnitPrice(goodsDetail.getPrice());
                orderDetail.setNum(goodsInfo.get(goodsId));
                totalPrice += NumberCalUtil.multiply(orderDetail.getUnitPrice(), orderDetail.getNum().doubleValue());
                orderDetail.setState(OrderDetail.STATE_NORMAL);
                orderDetails.add(orderDetail);
            }
        }
        /*
            private Long id;
            - private Long orderId;
            - private Long goodsId;
            - private Double unitPrice;
            - private Integer num;
            - private Integer state; // 10:正常 20:退款中 30:完成退款
         */
        // 4，生成订单数据
        Order order = new Order();
        order.setAccountId(3L);
        order.setAddressId(3L);
        Calendar calendar = Calendar.getInstance();
        calendar.set(NumberUtils.generateNumber(2018, 2020),
                NumberUtils.generateNumber(1, 12) - 1,
                NumberUtils.generateNumber(0, 28));
        Date time = calendar.getTime();
        order.setCreateTime(time);
        order.setState(Order.STATE_FINISHED);
        order.setTotalPrice(totalPrice);
        order.setValid(Order.VALID_NO);

        // 5，存储订单数据
        int i = orderMapper.insertSelective(order);

        if (i > 0) {
            // 6，存储订单详情
            orderDetails.forEach(od -> {
                od.setOrderId(order.getId());
                orderDetailMapper.insertSelective(od);
            });
        } else {
            throw new RuntimeException();
        }

        /*
            private Long id;
            -private Long accountId; (4)
            - private Long addressId; (4)
            private String logisticsId; // 物流id
            private String logisticsFlag; // 物流公司标识支持 申通快递:STO、圆通速递:YTO、百世快递:HTKY、天天快递:HHTT
           -- private Date createTime;
            -private Integer state; // 状态
            -private Double totalPrice; // 总价格
            -private Byte valid; // 1 有效 0 失效
         */

    }

    /**
     * 测试搜索数据的删除
     */
    public void testDeleteGoodsSearch() {
        GoodsSearch goodsSearch = new GoodsSearch();
        goodsSearch.setId(123L);
        goodsRepository.delete(goodsSearch);
    }

    /**
     * 测试新增搜索数据
     */
    public void testAddGoodsSearch() {
        this.amqpTemplate.convertAndSend("greenmall.goods.exchange", "goods.update.goods", 123L);
    }
}
