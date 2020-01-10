package com.troy.streamingexchange.binance.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BinanceOrderStatusEnum {
    NEW,
    PARTIALLY_FILLED,
    FILLED,
    CANCELED,
    PENDING_CANCEL,
    REJECTED,
    EXPIRED;

    @JsonCreator
    public static BinanceOrderStatusEnum getOrderStatus(String s) {
        try {
            return BinanceOrderStatusEnum.valueOf(s);
        } catch (Exception e) {
            throw new RuntimeException("Unknown order status " + s + ".");
        }
    }
}
