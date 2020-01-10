package com.troy.streamingexchange.gateio.dto;

/**
 * GateioWebsocketTypes
 *
 * @author liuxiaocheng
 * @date 2018/6/29
 */
public enum GateioWebsocketTypes {
    //验签
    SERVER_SIGN("server.sign"),
    //ping
    SERVER_PING("server.ping"),
    //time
    SERVER_TIME("server.time"),

    //实时行情
    TICKER_24_HR("ticker"),
    //最新市场成交
    TRADE("trades"),
    //k线
    KLINE("kline"),
    //盘口
    DEPTH("depth"),
    //委托
    ORDER("order"),

    //k线更新
    KLINE_UPDATE("kline.update"),
    //盘口更新
    DEPTH_UPDATE("depth.update"),
    //委托更新
    ORDER_UPDATE("order.update");

    /**
     * Get a type from the `type` string of a `GateioWebsocketTypes`.
     *
     * @param value
     * @return
     */
    public static GateioWebsocketTypes fromTransactionValue(String value) {
        for (GateioWebsocketTypes type : GateioWebsocketTypes.values()) {
            if (type.serializedValue.equals(value)) {
                return type;
            }
        }
        return null;
    }

    private String serializedValue;

    GateioWebsocketTypes(String serializedValue) {
        this.serializedValue = serializedValue;
    }

    public String getSerializedValue() {
        return serializedValue;
    }
}
