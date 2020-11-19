package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @description:销量
 * @author:leo_yuzhao
 * @date:2020/11/19
 */
@Data
@Table(name = "tb_sales_volume")

public class SalesVolume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "`year`")
    private Integer year;
    @Column(name = "`month`")
    private Integer month;
    private Long goodsId;
    private Long salesVolume;

    public SalesVolume() {
    }

    public SalesVolume(Integer year, Integer month, Long goodsId, Long salesVolume) {
        this.year = year;
        this.month = month;
        this.goodsId = goodsId;
        this.salesVolume = salesVolume;
    }

    public SalesVolume(Integer year, Integer month, Long goodsId) {
        this.year = year;
        this.month = month;
        this.goodsId = goodsId;
    }
}
