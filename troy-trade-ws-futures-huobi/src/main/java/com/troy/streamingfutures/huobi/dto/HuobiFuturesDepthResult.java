package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuobiFuturesDepthResult extends HuobiFuturesResult<HuobiFuturesDepth> {


    public HuobiFuturesDepthResult(
//            @JsonProperty("status") String status,
//                                   @JsonProperty("ts") Date ts,
                                   @JsonProperty("tick") HuobiFuturesDepth tick
//                                   @JsonProperty("ch") String ch,
//                                   @JsonProperty("err-code") String errCode,
//                                   @JsonProperty("err-msg") String errMsg
    ) {

        super(null, null, null, tick);
    }

//    public static HuobiFuturesDepthResult getInstance(HuobiFuturesDepth huobiFuturesDepth){
//        return new HuobiFuturesDepthResult(huobiFuturesDepth);
//    }

}