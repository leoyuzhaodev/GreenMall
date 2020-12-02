package com.yzf.greenmall.web;

import com.yzf.greenmall.entity.AdministrativeRegion;
import com.yzf.greenmall.service.AdministrativeRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    /**
     * 根据父id查找行政区划信息
     *
     * @return
     */
    @GetMapping(path = "/queryByFId/{fId}")
    public ResponseEntity<List<AdministrativeRegion>> queryByFId(@PathVariable(name = "fId") Long fId) {
        List<AdministrativeRegion> list = arService.findByFId(fId);
        if (CollectionUtils.isEmpty(list)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }



}
