package com.troy.streamingfutures.huobi.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HuobiFuturesUnSubscriptionMessage extends HuobiFuturesMessage{
    private static final String UNSUB = "unsub";
    private static final String ID = "id";

    @JsonProperty(UNSUB)
    private String unsub;

    @JsonProperty(ID)
    private String id;

    public HuobiFuturesUnSubscriptionMessage(String unsub, String id) {
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
