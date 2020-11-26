package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description:行政区划
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
@Table(name = "tb_administrative_region")
public class AdministrativeRegion {
    @Id
    private Long id; // 当前地址的编号
    private Long fatherId; // 当前地址的父编号
    private String name; // 当前地址名称
    private Integer level; // 当前地址层级

    public AdministrativeRegion() {
    }

    public AdministrativeRegion(Long id, Long fatherId, String name, Integer level) {
        this.id = id;
        this.fatherId = fatherId;
        this.name = name;
        this.level = level;
    }
}
