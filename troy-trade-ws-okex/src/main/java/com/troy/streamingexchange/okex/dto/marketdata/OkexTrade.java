package com.troy.streamingexchange.okex.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.commons.utils.DateUtils;

import java.util.Date;

public class OkexTrade {
    private final String timestamp;
    private final String price;
    private final String size;
    private final String tradeId;
    private final String side;
    private final String instrumentId;

    public OkexTrade(@JsonProperty("timestamp") String date, @JsonProperty("price") String price, @JsonProperty("amount") String size,
                       @JsonProperty("trade_id") String tradeId, @JsonProperty("side") String side, @JsonProperty("instrument_id") String instrumentId) {
        this.timestamp = DateUtils.formatDate(new Date());
        this.price = price;
        this.size = size;
        this.tradeId = tradeId;
        this.side = side;
        this.instrumentId = instrumentId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getSide() {
        return side;
    }

    public String getInstrumentId() {
        return instrumentId;
    }
}
