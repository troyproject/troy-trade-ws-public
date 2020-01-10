package com.troy.streamingexchange.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.streamingexchange.binance.dto.marketdata.BinanceOrderbook;

import java.util.List;

public class PartialDepthBinanceWebSocketTransaction {

    private final BinanceOrderbook orderBook;

    public PartialDepthBinanceWebSocketTransaction(
            @JsonProperty("lastUpdateId") long lastUpdateId,
            @JsonProperty("bids") List<Object[]> _bids,
            @JsonProperty("asks") List<Object[]> _asks
    ) {
        orderBook = new BinanceOrderbook(lastUpdateId, _bids, _asks);
    }

    public BinanceOrderbook getOrderBook() {
        return orderBook;
    }
}
