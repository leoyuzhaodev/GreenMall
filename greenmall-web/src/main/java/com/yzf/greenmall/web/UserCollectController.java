package com.yzf.greenmall.web;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.UserCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/11/30
 */
@Controller
@RequestMapping(path = "/collect")
public class UserCollectController {

    @Autowired
    private UserCollectService userCollectService;

    /**
     * 根据 goodsId 新增收藏
     *
     * @return
     */
    @GetMapping(path = "/add/{goodsId}")
    public ResponseEntity<Message> delete(@PathVariable(name = "goodsId") Long goodsId) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        Message message = userCollectService.add(loginUser, goodsId);
        return ResponseEntity.ok(message);
    }

    /**
     * 根据 goodsId 批量新增收藏
     *
     * @return
     */
    @PostMapping(path = "/addBatch")
    public ResponseEntity<Message> deleteBatch(@RequestBody List<Long> goodsIds) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        Message message = userCollectService.addBatch(loginUser, goodsIds);
        return ResponseEntity.ok(message);
    }
}
