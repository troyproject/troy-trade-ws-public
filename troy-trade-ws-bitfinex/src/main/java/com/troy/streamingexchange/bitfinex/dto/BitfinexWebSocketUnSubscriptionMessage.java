package com.troy.streamingexchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitfinexWebSocketUnSubscriptionMessage {
    private static final String EVENT = "event";
    private static final String CHANNEL_ID = "chanId";

    @JsonProperty(EVENT)
    private String event;

    @JsonProperty(CHANNEL_ID)
    private String channelId;

    public BitfinexWebSocketUnSubscriptionMessage(String channelId) {
        this.event = "unsubscribe";
        this.channelId = channelId;
    }

    public String getEvent() {
        return event;
    }

    public String getChannelId() {
        return channelId;
    }
}
