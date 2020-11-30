package com.yzf.greenmall.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description:用户收藏
 * @author:leo_yuzhao
 * @date:2020/11/30
 */
@Data
@Table(name = "tb_collect")
public class UserCollect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date createTime;
    private Long goodsId;
    private Long accountId;

    public UserCollect() {
    }

    public UserCollect(Date createTime, Long goodsId, Long accountId) {
        this.createTime = createTime;
        this.goodsId = goodsId;
        this.accountId = accountId;
    }
}
