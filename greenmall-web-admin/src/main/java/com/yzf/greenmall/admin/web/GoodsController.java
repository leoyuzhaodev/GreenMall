package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.common.WangEditorImage;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.service.GoodsService;
import com.yzf.greenmall.service.UploadService;
import com.yzf.greenmall.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @description:GoodsController
 * @author:leo_yuzhao
 * @date:2020/11/8
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private GoodsService goodsService;

    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @PostMapping(path = "/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = this.uploadService.upload(file);
        if (StringUtils.isBlank(url)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    /**
     * WangEditor富文本编辑器图片上传
     *
     * @param file
     * @return
     */
    @PostMapping(path = "/wangEditorImage")
    public ResponseEntity<WangEditorImage> uploadImageWangEditor(@RequestParam("file") MultipartFile file) {
        String url = this.uploadService.upload(file);
        if (StringUtils.isBlank(url)) {
            return ResponseEntity.badRequest().body(new WangEditorImage(1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new WangEditorImage(0, url));
    }

    /**
     * 添加商品/编辑商品
     *
     * @param goodsBo
     * @return
     */
    @PostMapping(path = "/update")
    public ResponseEntity<Message> add(@RequestBody GoodsBo goodsBo) {
        try {
            this.goodsService.update(goodsBo);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品保存成功");
        } catch (Exception e) {
            LOGGER.info("添加/编辑商品：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品保存失败");
    }

    /**
     * 根据商品id查询商品以及商品详情
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/queryGoodsBoById")
    public ResponseEntity<GoodsBo> queryGoodsBoById(@RequestParam("id") Long id) {
        try {
            GoodsBo goodsBo = this.goodsService.findGoodsBoById(id);
            return ResponseEntity.ok(goodsBo);
        } catch (Exception e) {
            LOGGER.info("根据商品id查询商品以及商品详情:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 分页查询商品
     *
     * @param queryPage
     * @return
     */
    @PostMapping(path = "/queryGoodsByPage")
    public ResponseEntity<LayuiPage<Goods>> findGoodsByPage(@RequestBody QueryPage<Goods> queryPage) {
        try {
            LayuiPage<Goods> goodsPage = this.goodsService.findGoodsByPage(queryPage);
            return ResponseEntity.ok(goodsPage);
        } catch (Exception e) {
            LOGGER.info("分页查询商品:服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除商品
     *
     * @param id
     * @return
     */
    @PostMapping(path = "/delete")
    public ResponseEntity<Message> delete(@RequestParam(name = "id", required = true) Long id) {
        try {
            this.goodsService.delete(id);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品删除成功");
        } catch (Exception e) {
            LOGGER.info("商品删除：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品删除失败");
    }

    /**
     * 撤销删除商品
     *
     * @param id
     * @return
     */
    @PostMapping(path = "/revocation")
    public ResponseEntity<Message> revocation(@RequestParam(name = "id", required = true) Long id) {
        try {
            this.goodsService.revocation(id);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品撤销删除成功");
        } catch (Exception e) {
            LOGGER.info("商品撤销删除：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品撤销删除失败");
    }

    /**
     * 商品上架
     *
     * @param id
     * @return
     */
    @PostMapping(path = "/goodsUp")
    public ResponseEntity<Message> goodsUp(@RequestParam(name = "id", required = true) Long id) {
        try {
            this.goodsService.goodsUp(id);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品上架成功");
        } catch (Exception e) {
            LOGGER.info("商品上架：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品上架失败");
    }

    /**
     * 商品下架
     *
     * @param id
     * @return
     */
    @PostMapping(path = "/goodsDown")
    public ResponseEntity<Message> goodsDown(@RequestParam(name = "id", required = true) Long id) {
        try {
            this.goodsService.goodsDown(id);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品下架成功");
        } catch (Exception e) {
            LOGGER.info("商品下架：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品下架失败");
    }

    /**
     * 商品批量删除
     *
     * @param ids
     * @return
     */
    @PostMapping(path = "/deleteBatch")
    public ResponseEntity<Message> deleteBatch(@RequestBody(required = true) List<Long> ids) {
        try {
            this.goodsService.deleteBatch(ids);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品批量删除成功");
        } catch (Exception e) {
            LOGGER.info("批量删除：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品批量删除失败");
    }


    /**
     * 商品批量撤销删除
     *
     * @param ids
     * @return
     */
    @PostMapping(path = "/revocationBatch")
    public ResponseEntity<Message> revocationBatch(@RequestBody(required = true) List<Long> ids) {
        try {
            this.goodsService.revocationBatch(ids);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品批量撤销删除成功");
        } catch (Exception e) {
            LOGGER.info("商品批量撤销删除失败：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品批量撤销删除失败");
    }

    /**
     * 商品批量上架
     *
     * @param ids
     * @return
     */
    @PostMapping(path = "/upBatch")
    public ResponseEntity<Message> upBatch(@RequestBody(required = true) List<Long> ids) {
        try {
            this.goodsService.upBatch(ids);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品批量上架成功");
        } catch (Exception e) {
            LOGGER.info("商品批量上架失败：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品批量上架失败");
    }

    /**
     * 商品批量下架
     *
     * @param ids
     * @return
     */
    @PostMapping(path = "/downBatch")
    public ResponseEntity<Message> downBatch(@RequestBody(required = true) List<Long> ids) {
        try {
            this.goodsService.downBatch(ids);
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品批量下架除成功");
        } catch (Exception e) {
            LOGGER.info("商品批量下架失败：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品批量下架失败");
    }



}
