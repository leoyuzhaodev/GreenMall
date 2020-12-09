package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Order;
import com.yzf.greenmall.entity.OrderDetail;
import com.yzf.greenmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/12/5
 */
@Controller
@RequestMapping(path = "/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);


    /**
     * 分页查询订单
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryOrderByPage")
    public ResponseEntity<LayuiPage<Order>> queryOrderByPage(@RequestBody QueryPage<Order> queryPage) {
        try {
            LayuiPage<Order> orderPage = orderService.findOrderByPage(queryPage);
            return ResponseEntity.ok(orderPage);
        } catch (Exception e) {
            LOGGER.info("分页查询订单:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 查询订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(path = "/queryOrderDetail")
    public ResponseEntity<List<OrderDetail>> queryOrderDetail(@RequestParam("id") Long orderId) {
        try {
            List<OrderDetail> list = orderService.findOrderDetail(orderId);
            if (CollectionUtils.isEmpty(list)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            LOGGER.info("查询订单详情:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 为订单设置物流单号
     *
     * @param logisticsInfo
     * @return
     */
    @PostMapping(path = "/setLogisticsInfo")
    public ResponseEntity<Message> setLogisticsInfo(@RequestBody Map<String, String> logisticsInfo) {
        try {
            Message message = orderService.setLogisticsInfo(logisticsInfo);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            LOGGER.info("为订单设置物流单号:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 统计指定年份 1-12月 的销售额
     *
     * @param year
     * @return
     */
    @GetMapping(path = "/statisticSaleroom")
    public ResponseEntity<List<Map<String, Object>>> statisticSaleroom(
            @RequestParam(name = "year", required = false) Integer year) {
        try {
            List<Map<String, Object>> info = orderService.statisticSaleroom(year);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            LOGGER.info("统计指定年份 1-12月 的销售额:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 统计指定年份，商品销售量top10
     *
     * @param year
     * @return
     */
    @GetMapping(path = "/statisticSalesvolume")
    public ResponseEntity<Map<String, Object>> statisticSalesvolume(
            @RequestParam(name = "year", required = false) Integer year) {
        try {
            Map<String, Object> info = orderService.statisticSalesvolume(year);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            LOGGER.info("统计指定年份，商品销售量top10:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 统计指定年份 1-12月 的用户注册量
     *
     * @param year
     * @return
     */
    @GetMapping(path = "/statisticRegistNum")
    public ResponseEntity<Map<String, Object>> statisticRegistNum(
            @RequestParam(name = "year", required = false) Integer year) {
        try {
            Map<String, Object> info = orderService.statisticRegistNum(year);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            LOGGER.info("统计指定年份 1-12月 的用户注册量:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
