package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Refund;
import com.yzf.greenmall.service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);


    /**
     * 分页查询退款申请
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryRefundByPage")
    public ResponseEntity<LayuiPage<Refund>> queryOrderByPage(@RequestBody QueryPage<Refund> queryPage) {
        try {
            LayuiPage<Refund> orderPage = refundService.findOrderByPage(queryPage);
            return ResponseEntity.ok(orderPage);
        } catch (Exception e) {
            LOGGER.info("分页查询退款申请:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

}
