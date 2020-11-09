package com.yzf.greenmall.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @description:UploadService
 * @author:leo_yuzhao
 * @date:2020/11/7
 */
@Service
@Transactional
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {

        // 1，校验文件类型
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            // 文件类型不合法
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }
        try {
            // 2，校验文件类容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                LOGGER.info("文件类型不合法：{}", originalFilename);
                return null;
            }
            // 保存到服务器
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            // 生成 url 地址返回
            return "http://image.greenmall.com/" + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}

