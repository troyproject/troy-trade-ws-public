package com.troy.trade.ws.constants;

public class WsScheduledConstant {


    /********* 数据同步相关 -- 开始 *************************/

    /**
     * 数据同步相关前缀
     */
    public static final String SYNC_REDIS_KEY_PREFIX = "sync:ws:";

    /**
     * 缓存中保存交易所数据前缀
     */
    public static final String EXCHANGE_DATA_REDIS_KEY_PREFIX = "exchange:ws:";

    /********* 合约信息同步相关 -- 开始 *************************/
    /**
     * 交易对同步相关前缀,mapKey为symbol_alias
     * exchange:ws:contractInfo:{exchCode}:symbol_alias
     */
    public static final String SYNC_CONTRACT_INFO_SYMBOL_ALIAS_MAP_REDIS_KEY = EXCHANGE_DATA_REDIS_KEY_PREFIX+"contractInfo:{exchCode}:symbol_alias";


    /**
     * 交易对同步相关前缀,mapKey为instrument_id
     * exchange:ws:contractInfo:{exchCode}:instrument_id
     */
    public static final String SYNC_CONTRACT_INFO_INSTRUMENT_ID_MAP_REDIS_KEY = EXCHANGE_DATA_REDIS_KEY_PREFIX+"contractInfo:{exchCode}:instrument_id";

    /**
     * 交易对同步超时时间 -- 秒
     */
    public static final Long SYNC_CONTRACT_INFO_MAP_TIME_OUT = 180L;

    /********* 交易对同步相关 -- 结束 *************************/

}
