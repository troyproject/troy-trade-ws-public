package com.troy.trade.ws.enums;

/**
 * 订单类型
 */
public enum OrderTypeEnum {
    /**
     * Buying order (the trader is providing the counter currency)
     */
    BID,
    /**
     * Selling order (the trader is providing the base currency)
     */
    ASK,
    /**
     * This is to close a short position when trading crypto currency derivatives such as swaps, futures for CFD's.
     */
    EXIT_ASK,
    /**
     * This is to close a long position when trading crypto currency derivatives such as swaps, futures for CFD's.
     */
    EXIT_BID
}
