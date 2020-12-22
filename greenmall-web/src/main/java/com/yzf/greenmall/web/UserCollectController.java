package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.UserCollectBo;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.UserCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
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

    /**
     * 加载收藏夹数据
     *
     * @return
     */
    @GetMapping(path = "/queryCollect/{loadNum}")
    public ResponseEntity<List<UserCollectBo>> queryCollect(@PathVariable(name = "loadNum") Long loadNum) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            List<UserCollectBo> lists = userCollectService.findCollect(loginUser, loadNum);
            return ResponseEntity.ok(lists);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除收藏
     *
     * @return
     */
    @GetMapping(path = "/deleteCollection/{collectId}")
    public ResponseEntity<Void> deleteCollection(@PathVariable(name = "collectId") Long collectId) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户信息加载异常...");
        }
        userCollectService.deleteCollection(loginUser, collectId);
        return ResponseEntity.ok().build();
    }
}
