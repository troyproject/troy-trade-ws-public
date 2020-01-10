package com.troy.streamingexchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class BitfinexWebSocketUpdateOrderbook extends BitfinexWebSocketOrderbookTransaction {
    public BitfinexOrderbookLevel level;

    public BitfinexWebSocketUpdateOrderbook() {
    }

    public BitfinexWebSocketUpdateOrderbook(BitfinexOrderbookLevel level) {
        this.level = level;
    }

    @Override
    public BitfinexOrderbook toBitfinexOrderBook(BitfinexOrderbook orderbook,boolean isRobot) {
        //更新book推送
        if(isRobot){
            orderbook.updateLevel(level);
        }else{
            //增量推送
            orderbook.pushLevel(level);
        }
        return orderbook;
    }
}
