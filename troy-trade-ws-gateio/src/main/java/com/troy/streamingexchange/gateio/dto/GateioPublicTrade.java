package com.troy.streamingexchange.gateio.dto;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.troy.streamingexchange.gateio.enums.GateioOrderTypeEnum;

import java.math.BigDecimal;
import java.util.Date;

/**
 * GateioPublicTrade
 *
 * @author liuxiaocheng
 * @date 2018/6/29
 */
public class GateioPublicTrade {
    @JSONField(name = "time")
    private final long timestamp;
    @JsonIgnore
    private final Date date;
    private final BigDecimal price;
    private final BigDecimal amount;
    @JSONField(name = "id")
    private final String tradeId;
    private final GateioOrderTypeEnum type;

    public GateioPublicTrade(long timestamp, Date date, BigDecimal price, BigDecimal amount, String tradeId, GateioOrderTypeEnum type) {
        this.timestamp = timestamp;
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.tradeId = tradeId;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTradeId() {
        return tradeId;
    }

    public GateioOrderTypeEnum getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GateioPublicTrade{" +
                "timestamp=" + timestamp +
                ", date=" + date +
                ", price=" + price +
                ", amount=" + amount +
                ", tradeId='" + tradeId + '\'' +
                ", type=" + type +
                '}';
    }
}
