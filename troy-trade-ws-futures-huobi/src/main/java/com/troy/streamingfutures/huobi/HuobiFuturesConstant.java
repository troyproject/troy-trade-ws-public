package com.troy.streamingfutures.huobi;

/**
 * HuobiFuturesConstant
 */
public class HuobiFuturesConstant {

    /**
     * K线（包含单位时间区间的开盘价、收盘价、最高价、最低价、成交量、成交额、成交笔数等数据 ）
     */
    public static final String KLINE_SUB_FORMATE = "market.%s.kline.%s";

    /**
     * 盘口深度
     */
    public static final String MARKET_DEPTH_SUB_FORMATE = "market.%s.depth.%s";

    /**
     * 成交记录（包含成交价格、成交量、成交方向等信息）
     */
    public static final String TRADE_DETAIL_SUB_FORMATE = "market.%s.trade.detail";

    /**
     * 实时行情（最近24小时成交量、成交额、开盘价、收盘价、最高价、最低价、成交笔数等）
     */
    public static final String MARKET_DETAIL_SUB_FORMATE = "market.%s.detail";
}
