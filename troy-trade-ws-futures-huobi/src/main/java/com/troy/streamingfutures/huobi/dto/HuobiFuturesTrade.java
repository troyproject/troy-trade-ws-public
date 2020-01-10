package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * HuobiFuturesTrade
 */
public final class HuobiFuturesTrade {
    private String amount;
    private Long ts;
    private String id;
    private String price;
    private String direction;

    @JsonCreator
    public HuobiFuturesTrade(@JsonProperty("amount") String amount, @JsonProperty("ts") Long ts, @JsonProperty("tradeId") String id,
                      @JsonProperty("price") String price, @JsonProperty("direction") String direction) {
        this.amount = amount;
        this.ts = ts;
        this.id = id;
        this.price = price;
        this.direction = direction;
    }

    public String getAmount() {
        return amount;
    }

    public Long getTs() {
        return ts;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getDirection() {
        return direction;
    }
}
