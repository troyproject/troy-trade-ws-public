package com.troy.trade.ws.constants;

import com.troy.commons.exchange.model.constant.ExchangeCode;

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public static Map<String,Integer> depthDefaultLimit = new HashMap<>();
    static {
        depthDefaultLimit.put(ExchangeCode.BINANCE.code(),50);
        depthDefaultLimit.put(ExchangeCode.HUOBI.code(),20);
        depthDefaultLimit.put(ExchangeCode.GATEIO.code(),30);
        depthDefaultLimit.put(ExchangeCode.OKEX.code(),30);
        depthDefaultLimit.put(ExchangeCode.BITFINEX.code(),30);
        depthDefaultLimit.put(ExchangeCode.OKEX_FUTURES_DELIVERY.code(),30);
        depthDefaultLimit.put(ExchangeCode.HUOBI_FUTURES_DELIVERY.code(),30);
    }

    public final static String CLIENTID = "troy-trade-ws";

    /**
     * 当前委托订阅前缀
     */
    public final static String ORDER_DESTINATION_PREFIX = "/topic/order/";

    /**
     * 当前委托订阅前缀
     */
    public final static String BALANCE_DESTINATION_PREFIX = "/topic/balance/";

    /**
     * 最新成交订阅前缀
     */
    public final static String TRADES_DESTINATION_PREFIX = "/topic/xxxxxs/";

    /**
     * 最新成交订阅前缀
     */
    public final static String DEPTH_DESTINATION_PREFIX = "/topic/depth/";

    /**
     * 账户session信息map key的分隔符
     */
    public final static String ACCOUNT_SESSION_KEY_SEPARATOR = "@";
}
