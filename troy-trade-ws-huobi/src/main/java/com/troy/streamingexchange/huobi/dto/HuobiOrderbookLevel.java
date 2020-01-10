package com.troy.streamingexchange.huobi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

/**
 * Created by Lukas Zaoralek on 8.11.17.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"price", "amount"})
public class HuobiOrderbookLevel {

    public BigDecimal price;

    public BigDecimal amount;


    public HuobiOrderbookLevel(BigDecimal price, BigDecimal amount) {
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
