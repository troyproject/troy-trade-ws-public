package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"price", "amount"})
public class HuobiFuturesOrderbookLevel {

    public BigDecimal price;

    public BigDecimal amount;


    public HuobiFuturesOrderbookLevel(BigDecimal price, BigDecimal amount) {
        this.price = price;
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
