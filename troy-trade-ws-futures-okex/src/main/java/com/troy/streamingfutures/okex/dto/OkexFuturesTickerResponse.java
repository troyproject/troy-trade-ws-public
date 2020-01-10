package com.troy.streamingfutures.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OkexFuturesTickerResponse {

    private final OkexFuturesTicker ticker;

    private long date;

    public OkexFuturesTickerResponse(@JsonProperty("ticker") OkexFuturesTicker ticker) {
        this.ticker = ticker;
    }

    public OkexFuturesTicker getTicker() {
        return ticker;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
