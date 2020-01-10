package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HuobiFuturesTradeResult
 */
public class HuobiFuturesTradeResult {
    private Long ts;
    private HuobiFuturesTrades tick;
    private String ch;

    @JsonCreator
    public HuobiFuturesTradeResult(@JsonProperty("ts") Long ts,
                            @JsonProperty("tick") HuobiFuturesTrades data,
                            @JsonProperty("ch") String ch) {
        this.ts = ts;
        this.tick = data;
        this.ch = ch;
    }

    public Long getTs() {
        return ts;
    }

    public HuobiFuturesTrades getTick() {
        return tick;
    }

    public String getCh() {
        return ch;
    }
}
