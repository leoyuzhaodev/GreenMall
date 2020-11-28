package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.Comment;
import com.yzf.greenmall.common.PageResult;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.service.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:EvaluateController
 * @author:leo_yuzhao
 * @date:2020/11/28
 */
@Controller
@RequestMapping(path = "/evaluate")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    @PostMapping(path = "/queryEvaluateByPage")
    public ResponseEntity<PageResult<Comment>> queryEvaluateByPage(@RequestBody QueryPage queryPage) {
        try {
            PageResult<Comment> pageResult = this.evaluateService.findEvaluateByPage(queryPage);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



}