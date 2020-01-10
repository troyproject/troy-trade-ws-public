package com.troy.streamingexchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.troy.streamingexchange.bitfinex.dto.marketdata.BitfinexTrade;

import java.math.BigDecimal;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class BitfinexWebSocketTrade {
    public long tradeId;
    public long timestamp;
    public BigDecimal amount;
    public BigDecimal price;

    public BitfinexWebSocketTrade() {
    }

    public BitfinexWebSocketTrade(long tradeId, long timestamp, BigDecimal amount, BigDecimal price) {
        this.tradeId = tradeId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.price = price;
    }

    public long getTradeId() {
        return tradeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BitfinexTrade toBitfinexTrade() {
        String type;
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            type = "sell";
        } else {
            type = "buy";
        }

        return new BitfinexTrade(price, amount, timestamp / 1000, "bitfinex", tradeId, type);
    }
}
