package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Evaluate;
import com.yzf.greenmall.entity.Refund;
import com.yzf.greenmall.service.EvaluateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @description:EvaluateController
 * @author:leo_yuzhao
 * @date:2020/12/13
 */
@Controller
@RequestMapping(path = "/evaluate")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);


    /**
     * 分页查询评论
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryEvaluateByPage")
    public ResponseEntity<LayuiPage<Evaluate>> queryOrderByPage(@RequestBody QueryPage<Evaluate> queryPage) {
        try {
            LayuiPage<Evaluate> evaluatePage = evaluateService.findEvaluateByPageAdmin(queryPage);
            return ResponseEntity.ok(evaluatePage);
        } catch (Exception e) {
            LOGGER.info("分页查询退款申请:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除或者撤销删除评论
     *
     * @param type 1：删除 0：撤销
     * @param evaluateId
     * @return
     */
    @GetMapping(path = "/valid")
    public ResponseEntity<Message> userValid(@RequestParam(name = "type") Integer type,
                                             @RequestParam(name = "evaluateId") Long evaluateId) {
        try {
            Message message = evaluateService.evaluateValid(type, evaluateId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
