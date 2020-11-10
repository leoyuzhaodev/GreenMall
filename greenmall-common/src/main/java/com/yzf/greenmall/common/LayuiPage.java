package com.yzf.greenmall.common;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:layui分页对象
 * @author:leo_yuzhao
 * @date:2020/11/10
 */
@Data
public class LayuiPage<T> implements Serializable {
    private int code = 0;
    private String msg = "";
    private Long count;
    private List<T> data;

    public LayuiPage() {
    }

    public LayuiPage(Long count, List<T> data) {
        this.count = count;
        this.data = data;
    }

    /**
     * 获取分页信息
     *
     * @param dataList
     */
    public LayuiPage<T> initLayuiPage(List<T> dataList) {
        PageInfo<T> pageInfo = new PageInfo(dataList);
        this.count = pageInfo.getTotal();
        this.data = pageInfo.getList();
        return this;
    }
}
