package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:CategoryController
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Controller
@RequestMapping(path = "/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    /**
     * 加载分类树
     *
     * @return
     */
    @GetMapping(path = "/queryCategoryTree")
    public ResponseEntity<List<CategoryTreeBo>> queryCategoryByFID() {
        List<CategoryTreeBo> categoryTreeBos = categoryService.loadCategoryTreeBo();
        if (CollectionUtils.isEmpty(categoryTreeBos)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryTreeBos);
    }


}
