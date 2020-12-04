package com.yzf.greenmall.web;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.SubmitOrderBo;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
