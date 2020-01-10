package com.troy.trade.ws.model.constant;

/**
 * 常量
 */
public class Constant {

    /**
     * 交易方向--买
     */
    public static final int DIRECTION_BUY = 1;
    /**
     * 交易方向--卖
     */
    public static final int DIRECTION_SELL = 2;


    public static String SUCCESS = "success";

    /**
     * 项目扫描包
     */
    public static final String PROJECT_BASE_PACKAGE = "com.troy";

    /**
     * 保存每个channel订阅的method信息 -- 前缀
     */
    public static final String SUBSCRIBE_CHANNEL_PUBLIC_REDIS_KEY_PREFIX = "webSocket:channel:public:{channelId}:";

    /**
     * 保存每个channel订阅的method信息
     */
    public static final String SUBSCRIBE_METHOD_PUBLIC_CHANNEL_REDIS_KEY = SUBSCRIBE_CHANNEL_PUBLIC_REDIS_KEY_PREFIX+"{exchCode}:{symbol}:{method}";


    /******* 机器人相关缓存redis key ************/
    public static final String ROBOT_INFO_REDIS_KEY = "robot:sub:";
    public static final String ROBOT_TIKCER_REDIS_KEY = ROBOT_INFO_REDIS_KEY+":ticker:{exchCode}:{symbol}";
    public static final String ROBOT_DEPTH_REDIS_KEY = ROBOT_INFO_REDIS_KEY+":depth:{exchCode}:{symbol}";

    /******* 交易所连接key ************/

    /*******************************/
    /**
     * 盘口请求全量数据最终时间 redis key
     */
    public static final String ORDERBOOK_GATEIO_FULLDATA_REDIS_KEY = "orderBook:fulldata:gateio:{sessionId}";

    /**
     * 盘口请求全量数据最终时间 redis key
     */
    public static final String ORDERBOOK_BITFINEX_FULLDATA_REDIS_KEY = "orderBook:fulldata:bitfinex:{sessionId}";
    /*********************************/

    /**
     * 买卖挂单最大返回给前端条数
     */
    public final static int MAX_DEPTH_SIZE = 30;

    /**
     * bitfinex 请求nonce取值key
     */
    public static final String HTTP_NONCE_BITFINEX = "http:nonce:bitfinex";



}
