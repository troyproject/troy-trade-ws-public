package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * HuobiFuturesTrades
 */
public final class HuobiFuturesTrades {
    private Long id;
    private Long ts;
    private List<HuobiFuturesTrade> data;

    @JsonCreator
    public HuobiFuturesTrades(@JsonProperty("id") Long id, @JsonProperty("ts") Long ts,
                             @JsonProperty("data") List<HuobiFuturesTrade> data) {
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

    public List<HuobiFuturesTrade> getData() {
        return data;
    }
}
