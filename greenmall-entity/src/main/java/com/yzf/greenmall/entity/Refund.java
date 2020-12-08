package com.yzf.greenmall.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yzf.greenmall.common.DoubleSerialize;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:退款实体
 * @author:leo_yuzhao
 * @date:2020/12/7
 */
@Data
@Table(name = "tb_refund")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long goodsId;
    private Long accountId;
    @JsonSerialize(using = DoubleSerialize.class)
    private Double unitPrice;
    private Integer num;
    @JsonSerialize(using = DoubleSerialize.class)
    private Double totalPrice;
    private Byte type; // 1,仅退款
    private Byte reasonType;
    private String description;
    private String images;
    private Date createTime;

    @Transient
    private String goodsImage;
    @Transient
    private String goodsTitle;
    @Transient
    private Byte refundState;

    private static final Byte REFUND_REASON_ABANDON = 1; // 不想要了
    private static final Byte REFUND_REASON_WRONG = 2; // 买错了
    private static final Byte REFUND_REASON_CHEAT = 3; // 与说明不符
    /**
     * 查询条件
     *
     * @return
     */
    public static Map<String, String> originalQueryMap() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("id,accountId,addressId", "");
        queryMap.put("state", "");
        return queryMap;
    }
}
