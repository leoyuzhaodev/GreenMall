package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:基础商品信息
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Data
@Table(name = "tb_goods")
public class Goods implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;// 标题
    private String subTitle;// 子标题
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private Boolean saleable;// 是否上架，true:可销售/上架 false:不可销售/下架
    private Date createTime;// 创建时间
    private Date lastUpdateTime;// 最后修改时间
    private Boolean valid;// 是否有效，逻辑删除用，true:有效 false:无效
    private Integer salesVolume;// 销量
    @Transient
    private String category;
    @Transient
    private GoodsDetail goodsDetail;

    public Goods() {
    }

    public Goods(Long id) {
        this.id = id;
    }

    public Goods(String title, String subTitle, Long cid1, Long cid2, Long cid3) {
        this.title = title;
        this.subTitle = subTitle;
        this.cid1 = cid1;
        this.cid2 = cid2;
        this.cid3 = cid3;
    }
}
