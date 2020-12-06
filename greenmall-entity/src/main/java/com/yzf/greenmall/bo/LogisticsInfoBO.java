package com.yzf.greenmall.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:物流信息
 * @author:leo_yuzhao
 * @date:2020/12/6
 */
@Data
public class LogisticsInfoBO {

    /**
     * 物流踪迹
     */
    @Data
    private static class Trace {
        @JsonProperty(value = "AcceptStation")
        private String acceptStation;

        @JsonProperty(value = "AcceptTime")
        private String acceptTime;
    }

    @JsonProperty(value = "LogisticCode")
    private String logisticCode;

    @JsonProperty(value = "ShipperCode")
    private String shipperCode;

    @JsonProperty(value = "Traces")
    private List<Trace> traces;

    @JsonProperty(value = "State")
    private String state;

    @JsonProperty(value = "Success")
    private Boolean success;

    @JsonProperty(value = "EBusinessID")
    private String eBusinessID;

}
