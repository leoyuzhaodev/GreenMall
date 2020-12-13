package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:Evaluate
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
@Table(name = "tb_evaluate")
public class Evaluate implements Serializable {

    public static final Byte VALID_YES = 1; // 订单有效
    public static final Byte VALID_NO = 0; // 订单失效

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long goodsId;
    private Long accountId;
    private Integer score;
    private String content;
    private String images;
    private Date createTime;
    private Byte valid; // 1 有效 0 失效

    @Transient
    private String goodsTitle; // 商品名称
    @Transient
    private String goodsImage; // 商品图片


    /**
     * 查询条件
     *
     * @return
     */
    public static Map<String, String> originalQueryMap() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("id,orderId,goodsId,accountId", "");
        return queryMap;
    }
}
