package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:商品分类
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Data
@Table(name = "tb_category")
public class Category implements Serializable {
    private static final String CATEGORY_LEVEL_ONE = "一级";
    private static final String CATEGORY_LEVEL_TWO = "二级";
    private static final String CATEGORY_LEVEL_THREE = "三级";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long parentId;
    // 注意isParent生成的getter和setter方法需要手动加上Is
    private Boolean isParent;
    @Transient
    private String level;


    /**
     * 生成初始的查询map
     *
     * @return
     */
    public static Map<String, String> originalQueryMap() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("id,name", "");
        return queryMap;
    }

    /**
     * 初始化分类的级别
     */
    public void initLevel() {
        if (this.parentId == 0) {
            this.level = Category.CATEGORY_LEVEL_ONE;
        } else if (this.parentId != 0 && this.isParent) {
            this.level = Category.CATEGORY_LEVEL_TWO;
        } else {
            this.level = Category.CATEGORY_LEVEL_THREE;
        }
    }
}
