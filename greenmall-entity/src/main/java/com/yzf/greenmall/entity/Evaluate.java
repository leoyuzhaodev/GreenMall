package com.yzf.greenmall.entity;

import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:Evaluate
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
@Table(name = "tb_evaluate")
public class Evaluate implements Serializable {
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
}
