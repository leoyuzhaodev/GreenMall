package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:订单
 * @author:leo_yuzhao
 * @date:2020/12/4
 */
@Data
@Table(name = "tb_order")
public class Order {
    public static final Integer STATE_PAYED_SEND = 10; // 完成付款 = 待发货
    public static final Integer STATE_SENT_SIGN = 20; // 已发货 = 代签收
    public static final Integer STATE_FINISHED = 30; // 交易完成
    public static final Integer STATE_CLOSED = 40; // 交易关闭
    public static final Byte VALID_YES = 1; // 订单有效
    public static final Byte VALID_NO = 0; // 订单失效
    public static final Byte EVALUATE_NO = 0; // 未评价
    public static final Byte EVALUATE_YES = 1; // 已评价

    public static final String LOGISTICS_FLAG_STO = "STO"; // 申通快递
    public static final String LOGISTICS_FLAG_YTO = "YTO"; // 圆通速递
    public static final String LOGISTICS_FLAG_HTKY = "HTKY"; // 百世快递
    public static final String LOGISTICS_FLAG_HHTT = "HHTT"; // 天天快递

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private Long addressId;
    private String logisticsId; // 物流id
    private String logisticsFlag; // 物流公司标识支持 申通快递:STO、圆通速递:YTO、百世快递:HTKY、天天快递:HHTT
    private Date createTime;
    private Integer state; // 状态
    private Double totalPrice; // 总价格
    private Byte valid; // 1 有效 0 失效

    @Transient
    private List<OrderDetail> orderDetailList;

    @Transient
    private Byte isNeedEvaluate; // 是否评价

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
