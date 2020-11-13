package com.yzf.greenmall.admin.web;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            return Message.generateResponseEntity(Message.MESSAGE_STATE_SUCCESS, "商品添加成功");
        } catch (Exception e) {
            LOGGER.info("添加商品：服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return Message.generateResponseEntity(Message.MESSAGE_STATE_ERROR, "商品添加失败");
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

    // 删除商品

    // 上架/下架商品


}
