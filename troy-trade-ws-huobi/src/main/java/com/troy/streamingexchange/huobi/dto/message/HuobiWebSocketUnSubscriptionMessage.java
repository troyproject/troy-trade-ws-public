package com.troy.streamingexchange.huobi.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HuobiWebSocketUnSubscriptionMessage extends HuobiWebSocketMessage{
    private static final String UNSUB = "unsub";
    private static final String ID = "id";

    @JsonProperty(UNSUB)
    private String unsub;

    @JsonProperty(ID)
    private String id;

    public HuobiWebSocketUnSubscriptionMessage(String unsub, String id) {
        this.unsub = unsub;
        this.id = id;
    }

    public String getUnsub() {
        return unsub;
    }

    public String getId() {
        return id;
    }
}
