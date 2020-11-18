package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.bo.CategoryTransferBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.entity.Param;
import com.yzf.greenmall.service.ParamService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据规格参数id加载分类关联穿梭框数据
     *
     * @param paramId
     * @return
     */
    @GetMapping(path = "/ctransfer")
    public ResponseEntity<CategoryTransferBo> queryParamsCategoryById(@RequestParam(value = "id", required = true) Long paramId) {
        try {
            CategoryTransferBo data = paramService.findParamsCategoryById(paramId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("根据规格参数id加载分类关联穿梭框数据:服务器内部错误：{}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 判断规格参数名称是否重复
     *
     * @param name
     * @return
     */
    @GetMapping(path = "/isNameRepeat")
    public ResponseEntity<Message> isNameRepeat(@RequestParam(required = true) String name, @RequestParam(required = true) Long id) {
        try {
            boolean flag = this.paramService.isNameRepeat(name, id);
            return ResponseEntity.ok(new Message(Message.MESSAGE_STATE_SUCCESS, flag ? "1" : "0"));
        } catch (Exception e) {
            LOGGER.info(" 判断规格参数名称是否重复：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 新增/更新规格参数
     *
     * @param param
     * @return
     */
    @PostMapping(path = "/update")
    public ResponseEntity<Message> update(@RequestBody Param param) {
        try {
            this.paramService.update(param);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "规格参数保存成功");
        } catch (Exception e) {
            LOGGER.info("添加/编辑规格参数：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "规格参数保存失败");
    }


    /**
     * 根据id查找规格参数
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/queryParamById")
    public ResponseEntity<Param> findParamById(@RequestParam(required = true) Long id) {
        try {
            Param param = this.paramService.findParamById(id);
            return ResponseEntity.ok(param);
        } catch (Exception e) {
            LOGGER.info("根据id查找规格参数：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 判断该分类是否可以取消与该规格参数的关联
     *
     * @param id
     * @param cid
     * @return
     */
    @GetMapping(path = "/canDelCategory")
    public ResponseEntity<Message> canDelCategory(@RequestParam(name = "id", required = true) Long id,
                                                  @RequestParam(name = "cid", required = true) Long cid) {
        try {
            boolean flag = this.paramService.canDelCategory(id, cid);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, flag ? "1" : "0");
        } catch (Exception e) {
            LOGGER.info("判断该分类是否可以取消与该规格参数的关联：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "判断该分类是否可以取消与该规格参数的关联");
    }

    /**
     * 删除规格参数
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/delete")
    public ResponseEntity<Message> delete(@RequestParam(name = "id", required = true) Long id) {
        try {
            boolean flag = this.paramService.delete(id);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, flag ? "删除成功" : "该规格参数有商品引用，无法删除");
        } catch (Exception e) {
            LOGGER.info("判断该分类是否可以取消与该规格参数的关联：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "判断该分类是否可以取消与该规格参数的关联");
    }
}
