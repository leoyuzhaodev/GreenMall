package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.GoodsSearch;
import com.yzf.greenmall.common.PageResult;
import com.yzf.greenmall.common.SearchRequest;
import com.yzf.greenmall.entity.Evaluate;
import com.yzf.greenmall.service.EvaluateService;
import com.yzf.greenmall.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:测试
 * @author:leo_yuzhao
 * @date:2020/11/28
 */
@Controller
@RequestMapping(path = "/test")
public class TestController {

    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private TestService testService;

    @PostMapping(path = "/test")
    public ResponseEntity<Void> search() {
        try {
            testService.testAddGoodsSearch();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
