package com.yzf.greenmall.web;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.service.UploadService;
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
 * @description:UploadController
 * @author:leo_yuzhao
 * @date:2020/12/1
 */
@Controller
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @PostMapping(path = "/image")
    public ResponseEntity<Message> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = this.uploadService.upload(file);
            if (StringUtils.isBlank(url)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new Message(1, url));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CREATED).body(new Message(2, ""));
        }
    }
}
