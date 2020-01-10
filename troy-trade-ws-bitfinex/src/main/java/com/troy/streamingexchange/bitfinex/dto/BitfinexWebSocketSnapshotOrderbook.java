package com.troy.streamingexchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class BitfinexWebSocketSnapshotOrderbook extends BitfinexWebSocketOrderbookTransaction {
    public BitfinexOrderbookLevel[] levels;

    @Override
    public BitfinexOrderbook toBitfinexOrderBook(BitfinexOrderbook orderbook,boolean isRobot) {
        return new BitfinexOrderbook(levels);
    }
}
