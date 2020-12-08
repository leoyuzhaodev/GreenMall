package com.yzf.greenmall.web;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Refund;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description:退款Controller
 * @author:leo_yuzhao
 * @date:2020/12/8
 */
@Controller
@RequestMapping(path = "/refund")
public class RefundController {

    @Autowired
    private RefundService refundService;

    /**
     * 用户提交退款申请
     *
     * @param refund
     * @return
     */
    @PostMapping(path = "/auth/refund")
    public ResponseEntity<Message> userRefund(@RequestBody Refund refund) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            Message message = refundService.userRefund(refund, loginUser);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询用户退款申请
     *
     * @return
     */
    @GetMapping(path = "/auth/queryRefund")
    public ResponseEntity<List<Refund>> queryRefund() {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            List<Refund> refundList = refundService.findRefund(loginUser);
            if (CollectionUtils.isEmpty(refundList)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(refundList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
