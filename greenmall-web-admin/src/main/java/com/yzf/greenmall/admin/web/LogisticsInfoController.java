package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.bo.LogisticsInfoBO;
import com.yzf.greenmall.service.KdniaoTrackQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:物流信息Controller
 * @author:leo_yuzhao
 * @date:2020/12/6
 */
@Controller
@RequestMapping(path = "/logistics")
public class LogisticsInfoController {

    @Autowired
    private KdniaoTrackQueryService kdniaoTrackQueryService;

    @GetMapping("/query")
    public ResponseEntity<LogisticsInfoBO> queryLogisticsInfo(@RequestParam("logisticsId") String logisticsId, @RequestParam("logisticsFlag") String logisticsFlag) {
        try {
            LogisticsInfoBO logisticsInfoBO = kdniaoTrackQueryService.queryLogisticsInfo(logisticsId, logisticsFlag);
            return ResponseEntity.ok(logisticsInfoBO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
