package com.troy.streamingexchange.huobi;

/**
 * HuobiProStreamingExchange
 *
 * @author liuxiaocheng
 * @date 2018/7/23
 */
public class HuobiProStreamingExchange extends HuobiStreamingExchange {
    private static final String API_URI = "wss://dddddd.huobi.pro/ws";

    public HuobiProStreamingExchange() {
        super(API_URI);
    }

}
