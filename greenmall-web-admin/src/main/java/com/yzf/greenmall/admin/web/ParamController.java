package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.entity.Param;
import com.yzf.greenmall.service.ParamService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);


    @RequestMapping(path = "/queryParamsByCid")
    public ResponseEntity<List<Param>> findParamsByCid(@RequestParam("cid3") Long cid3) {
        List<Param> params = paramService.findParamsByCid(cid3);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 分页查询规格参数
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryParamByPage")
    public ResponseEntity<LayuiPage<Param>> queryCategoryByPage(@RequestBody QueryPage<Param> queryPage) {
        try {
            LayuiPage<Param> paramPage = paramService.findParamByPage(queryPage);
            return ResponseEntity.ok(paramPage);
        } catch (Exception e) {
            LOGGER.info("分页查询规格参数:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
