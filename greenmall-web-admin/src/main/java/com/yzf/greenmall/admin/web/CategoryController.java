package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.entity.Goods;
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

    /**
     * 分页查询商品分类
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryCategoryByPage")
    public ResponseEntity<LayuiPage<Category>> queryCategoryByPage(@RequestBody QueryPage<Category> queryPage) {
        try {
            LayuiPage<Category> categoryPage = categoryService.findCategoryByPage(queryPage);
            return ResponseEntity.ok(categoryPage);
        } catch (Exception e) {
            LOGGER.info("分页查询分类:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * 根据分类级别查询商品分类
     *
     * @param level
     * @return
     */
    @GetMapping(path = "/level")
    public ResponseEntity<List<Category>> queryCategoryByLevel(@RequestParam(required = true) Byte level) {
        try {
            List<Category> categories = categoryService.findCategoryByLevel(level);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            LOGGER.info("根据分类级别查询商品分类:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 添加商品分类/编辑商品分类
     * {"name":"afafsd","level":"2","parentId":"1"}
     *
     * @param category
     * @return
     */
    @PostMapping(path = "/update")
    public ResponseEntity<Message> add(@RequestBody Category category) {
        try {
            this.categoryService.update(category);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品分类保存成功");
        } catch (Exception e) {
            LOGGER.info("添加/编辑商品分类：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品分类保存失败");
    }

    /**
     * 根据分类id查找分类
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/queryCategoryById")
    public ResponseEntity<Category> queryCategoryById(@RequestParam(required = true) Long id) {
        try {
            Category category = this.categoryService.findCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            LOGGER.info("根据分类id查找分类：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
