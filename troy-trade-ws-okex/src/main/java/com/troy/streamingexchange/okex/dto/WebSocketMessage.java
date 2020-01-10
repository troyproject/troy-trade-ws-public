package com.troy.streamingexchange.okex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketMessage {
    private final String op;
    private final String[] args;

    public WebSocketMessage(@JsonProperty("op") String op, @JsonProperty("args") String[] args) {
        this.op = op;
        this.args = args;
    }

    public String getOp() {
        return op;
    }

    public String[] getArgs() {
        return args;
    }
}
