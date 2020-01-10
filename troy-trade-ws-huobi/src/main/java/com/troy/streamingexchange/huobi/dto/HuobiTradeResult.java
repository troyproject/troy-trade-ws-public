package com.troy.streamingexchange.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HuobiTradeResult
 *
 * @author liuxiaocheng
 * @date 2018/7/12
 */
public class HuobiTradeResult {
    private Long ts;
    private HuobiTrades tick;
    private String ch;

    @JsonCreator
    public HuobiTradeResult(@JsonProperty("ts") Long ts,
                            @JsonProperty("tick") HuobiTrades data,
                            @JsonProperty("ch") String ch) {
        this.ts = ts;
        this.tick = data;
        this.ch = ch;
    }

    public Long getTs() {
        return ts;
    }

    public HuobiTrades getTick() {
        return tick;
    }

    public String getCh() {
        return ch;
    }
}
