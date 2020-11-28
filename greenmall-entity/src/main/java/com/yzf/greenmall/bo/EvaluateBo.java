package com.yzf.greenmall.bo;

import com.yzf.greenmall.common.PageResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:评价
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
public class EvaluateBo implements Serializable {
    private Long totalEvaluate; // 全部评价数量
    private Long goodEvaluateNum; // 好评数量
    private Long commonEvaluateNum; // 中评数量
    private Long badEvaluateNum; // 差评数量
    private Double goodEvaluateDegree; // 好评度
    private PageResult<Comment> pageResult; // 分页评价列表

}
