package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping(path = "/queryCategoryByFID")
    public ResponseEntity<List<Category>> queryCategoryByFID(@RequestParam("fId") Long fId) {
        List<Category> categories = categoryService.findCategoryByFID(fId);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping(path = "/queryCategoryTree")
    public ResponseEntity<List<CategoryTreeBo>> queryCategoryByFID() {
        List<CategoryTreeBo> categoryTreeBos = categoryService.loadCategoryTreeBo();
        if (CollectionUtils.isEmpty(categoryTreeBos)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryTreeBos);
    }
}
