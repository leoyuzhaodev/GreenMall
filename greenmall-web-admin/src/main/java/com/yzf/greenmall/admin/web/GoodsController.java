package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.common.WangEditorImage;
import com.yzf.greenmall.service.UploadService;
import com.yzf.greenmall.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
}
