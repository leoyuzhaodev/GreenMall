package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.LogisticsInfoBO;
import com.yzf.greenmall.service.KdniaoTrackQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/queryLogisticsInfo/{orderId}")
    public ResponseEntity<LogisticsInfoBO> queryLogisticsInfoByOrderId(@PathVariable(name = "orderId") Long orderId) {
        try {
            LogisticsInfoBO logisticsInfoBO = kdniaoTrackQueryService.findLogisticsInfoByOrderId(orderId);
            return ResponseEntity.ok(logisticsInfoBO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
