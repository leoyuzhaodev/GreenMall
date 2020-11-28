package com.yzf.greenmall.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:列表项
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
public class Comment implements Serializable {
    private String userNickName; // 用户名：b***1
    private String userPortrait; // 用户头像
    private Date date;// 评论时间：111
    private String content; // 评论文字：xxxx
    private String images; // 评论图片：
}