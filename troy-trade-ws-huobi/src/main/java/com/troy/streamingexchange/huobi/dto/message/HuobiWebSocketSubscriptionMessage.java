package com.troy.streamingexchange.huobi.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.streamingexchange.huobi.TopicType;


public class HuobiWebSocketSubscriptionMessage extends HuobiWebSocketMessage {
    private static final String KLINE_SUB_FORMATE = "market.%s.kline.%s";
    private static final String MARKET_DEPTH_SUB_FORMATE = "market.%s.depth.%s";
    private static final String TRADE_DETAIL_SUB_FORMATE = "market.%s.trade.detail";
    private static final String MARKET_DETAIL_SUB_FORMATE = "market.%s.detail";
    private static final String SUB = "sub";
    private static final String ID = "id";


    @JsonProperty(SUB)
    private String sub;

    @JsonProperty(ID)
    private String id;

    @JsonIgnore
    private String pair;

    public HuobiWebSocketSubscriptionMessage(String channelType, String id, String pair, String period, String depthType) {
        if (TopicType.TOPIC_KLINE.equals(channelType)) {
            this.sub = String.format(KLINE_SUB_FORMATE, pair, period);
        }
        if (TopicType.TOPIC_MARKET_DEPTH.equals(channelType)) {
            this.sub = String.format(MARKET_DEPTH_SUB_FORMATE, pair, depthType);
        }
        if (TopicType.TOPIC_MARKET_TRADE.equals(channelType)) {
            this.sub = String.format(TRADE_DETAIL_SUB_FORMATE, pair);
        }
        if (TopicType.TOPIC_MARKET_DETAIL.equals(channelType)) {
            this.sub = String.format(MARKET_DETAIL_SUB_FORMATE, pair);

        }
        this.id = id;
        this.pair = pair;
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

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }
}
