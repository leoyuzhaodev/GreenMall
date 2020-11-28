package com.yzf.greenmall.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:商品分类树
 * @author:leo_yuzhao
 * @date:2020/11/13
 */
@Data
public class CategoryTreeBo implements Serializable {
    private String title;
    private Long id;
    private List<CategoryTreeBo> children;

    public CategoryTreeBo() {
    }

    public CategoryTreeBo(String title, Long id) {
        this.title = title;
        this.id = id;
    }

}
