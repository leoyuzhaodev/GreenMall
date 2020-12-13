package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.OrderDetailItemBo;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Order;
import com.yzf.greenmall.entity.SubmitOrderBo;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:OrderController
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Controller
@RequestMapping(path = "/order/auth")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     *
     * @return
     */
    @PostMapping(path = "/submitOrder")
    public ResponseEntity<Message> submitOrder(@RequestBody List<SubmitOrderBo> list) {

        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            Message message = orderService.submitOrder(loginUser, list);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询登录用户的所有订单
     *
     * @return
     */
    @GetMapping(path = "/queryOrder")
    public ResponseEntity<List<Order>> queryOrder() {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            List<Order> list = orderService.findOrder(loginUser);
            if (CollectionUtils.isEmpty(list)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询登录用户的所有订单
     *
     * @return
     */
    @GetMapping(path = "/confirmReceipt/{orderId}")
    public ResponseEntity<Message> confirmReceipt(@PathVariable(name = "orderId") Long orderId) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            Message message = orderService.confirmReceipt(orderId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除订单
     *
     * @return
     */
    @GetMapping(path = "/deleteOrder/{orderId}")
    public ResponseEntity<Message> deleteOrder(@PathVariable(name = "orderId") Long orderId) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            Message message = orderService.deleteOrder(orderId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询用户的订单详情项
     *
     * @return
     */
    @GetMapping(path = "/queryUserOrderDetailItem/{type}")
    public ResponseEntity<List<OrderDetailItemBo>> queryUserOrderDetailItem(
            @PathVariable(name = "type", required = false) Integer type) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            List<OrderDetailItemBo> items = orderService.findUserOrderDetailItem(type, loginUser);
            if (CollectionUtils.isEmpty(items)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
