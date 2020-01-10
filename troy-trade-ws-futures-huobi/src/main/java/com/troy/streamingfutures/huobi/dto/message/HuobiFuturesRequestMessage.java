package com.troy.streamingfutures.huobi.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HuobiWebSocketRequestMessage
 *
 * @author liuxiaocheng
 * @date 2018/7/13
 */
public class HuobiFuturesRequestMessage extends HuobiFuturesMessage {
    private static final String REQ = "req";
    private static final String ID = "id";

    @JsonProperty(REQ)
    private String req;

    @JsonProperty(ID)
    private String id;

    public HuobiFuturesRequestMessage(String req, String id) {
        this.req = req;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getReq() {
        return req;
    }
}
