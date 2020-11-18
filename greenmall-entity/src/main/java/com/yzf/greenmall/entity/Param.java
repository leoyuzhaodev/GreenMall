package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:规格参数
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Data
@Table(name = "tb_param")
public class Param implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 参数id
    private String name; // 参数名
    @Column(name = "`numeric`") // numeric 在mysql中是关键字
    private Boolean numeric;// 是否是数字类型的参数 true false
    private String unit; // 规格此参数单位，没有单位填：无
    @Transient
    private String cNames;
    @Transient
    private List<Category> categories;

    /**
     * 生成初始的查询map
     *
     * @return
     */
    public static Map<String, String> originalQueryMap() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("id,name,unit", "");
        return queryMap;
    }
}
