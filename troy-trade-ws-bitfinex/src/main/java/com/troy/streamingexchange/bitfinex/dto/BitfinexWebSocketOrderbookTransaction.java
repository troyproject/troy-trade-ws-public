package com.troy.streamingexchange.bitfinex.dto;

public abstract class BitfinexWebSocketOrderbookTransaction {
    public String channelId;

    public BitfinexWebSocketOrderbookTransaction() {
    }

    public BitfinexWebSocketOrderbookTransaction(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public abstract BitfinexOrderbook toBitfinexOrderBook(BitfinexOrderbook orderbook,boolean isRobot);
}
