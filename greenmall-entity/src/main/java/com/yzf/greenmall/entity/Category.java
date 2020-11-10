package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:商品分类
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Data
@Table(name = "tb_category")
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long parentId;
    // 注意isParent生成的getter和setter方法需要手动加上Is
    private Boolean isParent;
}
