package com.troy.streamingfutures.huobi.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HuobiFuturesSubscriptionMessage extends HuobiFuturesMessage {
    private static final String SUB = "sub";
    private static final String ID = "id";

    @JsonProperty(SUB)
    private String sub;

    @JsonProperty(ID)
    private String id;

    public HuobiFuturesSubscriptionMessage(String sub, String id) {
        this.sub = sub;
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
