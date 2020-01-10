package com.troy.streamingexchange.bitfinex.dto;

import com.troy.streamingexchange.bitfinex.dto.marketdata.BitfinexTrade;

public abstract class BitfinexWebSocketTradesTransaction {
    public String channelId;

    public BitfinexWebSocketTradesTransaction() {
    }

    public BitfinexWebSocketTradesTransaction(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public abstract BitfinexTrade[] toBitfinexTrades();
}
