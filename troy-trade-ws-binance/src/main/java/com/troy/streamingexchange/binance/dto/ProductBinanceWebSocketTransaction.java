package com.troy.streamingexchange.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.trade.ws.dto.currency.CurrencyPair;

public class ProductBinanceWebSocketTransaction extends BaseBinanceWebSocketTransaction {

    protected final CurrencyPair currencyPair;

    public ProductBinanceWebSocketTransaction(
            @JsonProperty("e") String eventType,
            @JsonProperty("E") String eventTime,
            @JsonProperty("s") String symbol) {
        super(eventType, eventTime);
        currencyPair = BinanceAdapters.adaptSymbol(symbol);
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }
}
