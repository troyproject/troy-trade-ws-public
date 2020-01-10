package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * HuobiFuturesTradeRquestResult
 */
public class HuobiFuturesTradeRquestResult {
    private Long id;
    private String status;
    private String rep;
    private List<HuobiFuturesTrade> data;

    @JsonCreator
    public HuobiFuturesTradeRquestResult(@JsonProperty("id") Long id, @JsonProperty("status") String status,
                                  @JsonProperty("rep") String rep, @JsonProperty("data") List<HuobiFuturesTrade> data) {
        this.id = id;
        this.status = status;
        this.rep = rep;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getRep() {
        return rep;
    }

    public List<HuobiFuturesTrade> getData() {
        return data;
    }
}
