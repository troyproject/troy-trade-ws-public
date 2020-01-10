package com.troy.trade.ws.model.enums;

public enum MethodEnum {

    /*********** 公共订阅 *************/
    //system相关消息
    PING("server.ping"),

    //depth订阅相关
    DEPTH_QUERY("depth.query"),
    DEPTH_SUBSCRIBE("depth.subscribe"),
    DEPTH_UPDATE("depth.update"),
    DEPTH_UNSUBSCRIBE("depth.unsubscribe"),

    //depthchart订阅相关
    DEPTH_CHART_QUERY("depthchart.query"),
    DEPTH_CHART_SUBSCRIBE("depthchart.subscribe"),
    DEPTH_CHART_UPDATE("depthchart.update"),
    DEPTH_CHART_UNSUBSCRIBE("depthchart.unsubscribe"),

    //kline订阅相关
    KLINE_QUERY("kline.query"),
    KLINE_SUBSCRIBE("kline.subscribe"),
    KLINE_UPDATE("kline.update"),
    KLINE_UNSUBSCRIBE("kline.unsubscribe"),

    //ticker 订阅相关
    TICKER_QUERY("ticker.query"),
    TICKER_SUBSCRIBE("ticker.subscribe"),
    TICKER_UPDATE("ticker.update"),
    TICKER_UNSUBSCRIBE("ticker.unsubscribe"),

    //xxxxx 订阅相关
    TRADE_QUERY("trades.query"),
    TRADE_SUBSCRIBE("trades.subscribe"),
    TRADE_UPDATE("trades.update"),
    TRADE_UNSUBSCRIBE("trades.unsubscribe"),

    /*********** 需要验权订阅 *************/
    //验权
    SIGN_AUTH("server.sign"),

    //order订阅相关-当前挂单
    ORDER_QUERY("order.query"),
    ORDER_SUBSCRIBE("order.subscribe"),
    ORDER_UPDATE("order.update"),
    ORDER_UNSUBSCRIBE("order.unsubscribe"),

    //balance订阅相关-当前挂单
    BALANCE_QUERY("balance.query"),
    BALANCE_SUBSCRIBE("balance.subscribe"),
    BALANCE_UPDATE("balance.update"),
    BALANCE_UNSUBSCRIBE("balance.unsubscribe"),
    ;



    private String type;//类型

    private String defaultValue;//类型

    MethodEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
