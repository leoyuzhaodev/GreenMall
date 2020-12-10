package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.service.EvaluateService;
import com.yzf.greenmall.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
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
    private TestService testService;

    @PostMapping(path = "/test")
    public ResponseEntity<Void> search() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
