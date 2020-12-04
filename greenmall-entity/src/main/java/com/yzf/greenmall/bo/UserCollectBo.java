package com.yzf.greenmall.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yzf.greenmall.common.DoubleSerialize;
import lombok.Data;

/**
 * @description:用户收藏
 * @author:leo_yuzhao
 * @date:2020/11/30
 */
@Data
public class UserCollectBo {
    private Long id;
    private Long goodsId;
    private String image;
    private String title;
    private String subTitle;
    @JsonSerialize(using = DoubleSerialize.class)
    private Double price;
    private Double goodEvaluateDegree;
    private Long saleMonth;
    private int valid; // 1：有效 0：失效
}
