package com.troy.streamingexchange.huobi;

/**
 * TopicType
 *
 * @author liuxiaocheng
 * @date 2018/7/25
 */
public class TopicType {
    /**
     * K线（包含单位时间区间的开盘价、收盘价、最高价、最低价、成交量、成交额、成交笔数等数据 ）
     */
    public static final String TOPIC_KLINE = "market.kline";
    /**
     * 盘口深度
     */
    public static final String TOPIC_MARKET_DEPTH = "market.depth";
    /**
     * 成交记录（包含成交价格、成交量、成交方向等信息）
     */
    public static final String TOPIC_MARKET_TRADE = "market.trade";
    /**
     * 实时行情（最近24小时成交量、成交额、开盘价、收盘价、最高价、最低价、成交笔数等）
     */
    public static final String TOPIC_MARKET_DETAIL = "market.detail";


    /**
     * 成交记录请求（包含成交价格、成交量、成交方向等信息）
     */
    public static final String TOPIC_MARKET_TRADE_REQ = "market.trade_req";
}
