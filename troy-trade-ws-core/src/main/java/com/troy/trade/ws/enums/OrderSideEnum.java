package com.troy.trade.ws.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderSideEnum {
    BUY,
    SELL;

    @JsonCreator
    public static OrderSideEnum getOrderSide(String s) {
        try {
            return OrderSideEnum.valueOf(s);
        } catch (Exception e) {
            throw new RuntimeException("Unknown order side " + s + ".");
        }
    }
}
