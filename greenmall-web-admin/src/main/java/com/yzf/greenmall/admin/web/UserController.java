package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Refund;
import com.yzf.greenmall.entity.User;
import com.yzf.greenmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/12/8
 */
@Controller
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);


    /**
     * 分页查询用户
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryUserByPage")
    public ResponseEntity<LayuiPage<User>> queryOrderByPage(@RequestBody QueryPage<User> queryPage) {
        try {
            LayuiPage<User> orderPage = userService.findOrderByPage(queryPage);
            return ResponseEntity.ok(orderPage);
        } catch (Exception e) {
            LOGGER.info("分页查询用户:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * 禁用或者解禁用户
     * @param type
     * @param userId
     * @return
     */
    @GetMapping(path = "/valid")
    public ResponseEntity<Message> userValid(@RequestParam(name = "type") Integer type,
                                             @RequestParam(name = "userId") Long userId) {
        try {
            Message message = userService.userValid(type, userId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
