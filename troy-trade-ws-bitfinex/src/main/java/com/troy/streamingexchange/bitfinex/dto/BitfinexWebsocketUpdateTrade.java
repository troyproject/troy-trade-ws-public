package com.troy.streamingexchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.troy.streamingexchange.bitfinex.dto.marketdata.BitfinexTrade;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class BitfinexWebsocketUpdateTrade extends BitfinexWebSocketTradesTransaction {
    public String type;
    public BitfinexWebSocketTrade trade;

    public BitfinexWebsocketUpdateTrade() {
    }

    public BitfinexWebsocketUpdateTrade(String channelId, String type, BitfinexWebSocketTrade trade) {
        super(channelId);
        this.type = type;
        this.trade = trade;
    }

    public String getType() {
        return type;
    }

    public BitfinexWebSocketTrade getTrade() {
        return trade;
    }

    public BitfinexTrade[] toBitfinexTrades() {
        return new BitfinexTrade[]{trade.toBitfinexTrade()};
    }
}
