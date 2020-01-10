package com.troy.streamingexchange.huobi.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HuobiWebSocketRequestMessage
 *
 * @author liuxiaocheng
 * @date 2018/7/13
 */
public class HuobiWebSocketRequestMessage extends HuobiWebSocketMessage {
    private static final String TRADE_DETAIL_REQ_FORMATE = "market.%s.trade.detail";
    private static final String REQ = "req";
    private static final String ID = "id";

    @JsonProperty(REQ)
    private String req;

    @JsonProperty(ID)
    private String id;

    @JsonIgnore
    private String pair;

    public HuobiWebSocketRequestMessage(String id, String pair) {
        this.req = String.format(TRADE_DETAIL_REQ_FORMATE, pair);
        this.id = id;
        this.pair = pair;
    }

    public String getId() {
        return id;
    }

    public String getPair() {
        return pair;
    }

    public String getReq() {
        return req;
    }
}
