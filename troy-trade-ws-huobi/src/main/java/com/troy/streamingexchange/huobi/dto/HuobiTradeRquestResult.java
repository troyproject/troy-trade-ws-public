package com.troy.streamingexchange.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * HuobiTradeRquestResult
 *
 * @author liuxiaocheng
 * @date 2018/7/13
 */
public class HuobiTradeRquestResult {
    private Long id;
    private String status;
    private String rep;
    private List<HuobiTrade> data;

    @JsonCreator
    public HuobiTradeRquestResult(@JsonProperty("id") Long id, @JsonProperty("status") String status,
                                  @JsonProperty("rep") String rep, @JsonProperty("data") List<HuobiTrade> data) {
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

    public List<HuobiTrade> getData() {
        return data;
    }
}
