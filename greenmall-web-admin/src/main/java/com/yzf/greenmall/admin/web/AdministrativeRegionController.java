package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.service.AdministrativeRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:AdministrativeRegionController
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Controller
@RequestMapping(path = "/ar")
public class AdministrativeRegionController {

    @Autowired
    private AdministrativeRegionService arService;

    @GetMapping(path = "/init")
    public ResponseEntity<Void> init() {
        try {
            arService.initAR();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
