package com.yzf.greenmall.entity;

import lombok.Data;
import sun.rmi.runtime.Log;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private Date lastUpdateTime;// 最后修改时间 last_update_time lastUpdateTime
    private Boolean valid;// 是否有效，逻辑删除用，true:有效 false:无效
    @Transient
    private Long salesVolume;// 销量
    @Transient
    private String category;
    @Transient
    private GoodsDetail goodsDetail;
    @Transient
    private Long stock; // 商品库存

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

    /**
     * 生成初始的查询map
     *
     * @return
     */
    public static Map<String, String> originalQueryMap() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("title,subTitle,id", "");
        queryMap.put("saleable", "1");
        queryMap.put("valid", "1");
        queryMap.put("valid", "1");
        return queryMap;
    }
}
