package com.troy.streamingexchange.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * HuobiTrades
 *
 * @author liuxiaocheng
 * @date 2018/7/12
 */
public final class HuobiTrades {
    private Long id;
    private Long ts;
    private List<HuobiTrade> data;

    @JsonCreator
    public HuobiTrades(@JsonProperty("id") Long id, @JsonProperty("ts") Long ts, @JsonProperty("data") List<HuobiTrade> data) {
        this.id = id;
        this.ts = ts;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Long getTs() {
        return ts;
    }

    public List<HuobiTrade> getData() {
        return data;
    }
}
