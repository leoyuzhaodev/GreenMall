package com.yzf.greenmall.common;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:WangEditor图片上传，返回数据格式
 * @author:leo_yuzhao
 * @date:2020/11/8
 */
@Data
public class WangEditorImage implements Serializable {

    // errno 即错误代码，0 表示没有错误。
    // 如果有错误，errno != 0，可通过下文中的监听函数 fail 拿到该错误码进行自定义处理
    private Integer errno = 0;

    // 存放图片地址
    private List<String> data = new ArrayList<>();

    public WangEditorImage(Integer errno, String imageSrc) {
        this.errno = errno;
        this.data.add(imageSrc);
    }

    public WangEditorImage(Integer errno) {
        this.errno = errno;
    }
}
