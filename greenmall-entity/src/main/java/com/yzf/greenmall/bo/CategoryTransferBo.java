package com.yzf.greenmall.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description:分类穿梭框
 * @author:leo_yuzhao
 * @date:2020/11/16
 */
@Data
public class CategoryTransferBo implements Serializable {
    private List<Map<String, Object>> categories; // {id:2,name:""}
    private List<Long> checkedList; // [1,2]

    public CategoryTransferBo() {
    }

    public CategoryTransferBo(List<Map<String, Object>> categories, List<Long> checkedList) {
        this.categories = categories;
        this.checkedList = checkedList;
    }
}
