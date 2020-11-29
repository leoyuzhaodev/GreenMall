package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.ShopCartBo;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.ShopCart;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.ShopCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:购物车Controller
 * @author:leo_yuzhao
 * @date:2020/11/28
 */
@Controller
@RequestMapping(path = "/shopCart")
public class ShopCartController {

    @Autowired
    private ShopCartService shopCartService;

    /**
     * 加购物车
     *
     * @param shopCart
     * @return
     */
    @PostMapping(path = "/add")
    public ResponseEntity<Message> add(@RequestBody ShopCart shopCart) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        Message message = shopCartService.add(shopCart, loginUser, false);
        return ResponseEntity.ok(message);
    }

    /**
     * 加购物车
     *
     * @param shopCart
     * @return
     */
    @PostMapping(path = "/update")
    public ResponseEntity<Message> update(@RequestBody ShopCart shopCart) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        Message message = shopCartService.add(shopCart, loginUser, true);
        return ResponseEntity.ok(message);
    }

    /**
     * 购物车展示
     *
     * @return
     */
    @GetMapping(path = "/queryCart")
    public ResponseEntity<List<ShopCartBo>> findCart() {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        List<ShopCartBo> shopCartBos = shopCartService.findCart(loginUser);
        return ResponseEntity.ok(shopCartBos);
    }

    /**
     * 购物车展示
     *
     * @return
     */
    @GetMapping(path = "/delete/{goodsId}")
    public ResponseEntity<Message> delete(@PathVariable(name = "goodsId") Long goodsId) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        Message message = shopCartService.delete(loginUser, goodsId);
        return ResponseEntity.ok(message);
    }
}
