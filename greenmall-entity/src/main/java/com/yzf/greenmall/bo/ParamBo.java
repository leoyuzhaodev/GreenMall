package com.yzf.greenmall.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * 规格参数，用于接收此数据：
 * [{"id":"1","value":"2"},{"id":"2","value":"湖北"}]
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Data
public class ParamBo implements Serializable {
    private Long id;
    private String value;
}
