package com.troy.streamingexchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitfinexWebSocketSubscriptionMessage {

    private static final String EVENT = "event";
    private static final String CHANNEL = "channel";
    private static final String PAIR = "pair";
    private static final String PREC = "prec";
    private static final String LEN = "len";
    private static final String FREQ = "freq";

    @JsonProperty(EVENT)
    private String event;

    @JsonProperty(CHANNEL)
    private String channel;

    @JsonProperty(PAIR)
    private String pair;

    @JsonProperty(PREC)
    private String prec;

    @JsonProperty(FREQ)
    private String freq;

    @JsonProperty(LEN)
    private String len;

    public BitfinexWebSocketSubscriptionMessage(String channel, String pair) {
        this.event = "subscribe";
        this.channel = channel;
        this.pair = pair;
    }

    public BitfinexWebSocketSubscriptionMessage(String channel, String pair, String prec, String len) {
        this.event = "subscribe";
        this.channel = channel;
        this.pair = pair;
        this.prec = prec;
        this.len = len;
    }

    public BitfinexWebSocketSubscriptionMessage(String channel, String pair, String prec, String freq, String len) {
        this.event = "subscribe";
        this.channel = channel;
        this.pair = pair;
        this.prec = prec;
        this.freq = freq;
        this.len = len;
    }

    public String getEvent() {
        return event;
    }

    public String getChannel() {
        return channel;
    }

    public String getPair() {
        return pair;
    }

    public String getPrec() {
        return prec;
    }

    public String getLen() {
        return len;
    }

    public String getFreq() {
        return freq;
    }
}
