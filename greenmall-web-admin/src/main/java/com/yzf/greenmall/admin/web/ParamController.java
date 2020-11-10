package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.entity.Param;
import com.yzf.greenmall.service.ParamService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description:ParamController
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Controller
@RequestMapping(path = "/param")
public class ParamController {

    @Autowired
    private ParamService paramService;

    @RequestMapping(path = "/queryParamsByCid")
    public ResponseEntity<List<Param>> findParamsByCid(@RequestParam("cid3") Long cid3) {
        List<Param> params = paramService.findParamsByCid(cid3);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }
}
