package com.troy.streamingexchange.okex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.okex.dto.WebSocketMessage;
import com.troy.trade.ws.exceptions.ExchangeException;
import com.troy.trade.ws.netty.JsonNettyStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OkexStreamingService extends JsonNettyStreamingService {

    private static final String ERROR = "error";

    private static final Logger LOG = LoggerFactory.getLogger(OkexStreamingService.class);

    public OkexStreamingService(String apiUrl) {
        super(apiUrl);
    }

    @Override
    public String getChannelNameFromMessage(JsonNode message) throws IOException {
        if(message.get("channel") != null){
            return message.get("channel").asText();
        }else if(message.get("table") != null){
            return message.get("table").asText() + ":"+message.get("data").get(0).get("instrument_id").asText();
        }
        return null;
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        String[] channelNames = {channelName};
        WebSocketMessage webSocketMessage = new WebSocketMessage("subscribe", channelNames);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(webSocketMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        String[] channelNames = {channelName};
        WebSocketMessage webSocketMessage = new WebSocketMessage("unsubscribe", channelNames);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(webSocketMessage);
    }

    @Override
    protected void handleMessage(JsonNode message) {
        JsonNode event = message.get("event");
        if(event != null && event.textValue().equals("subscribe")){
            return;
        }else if(event != null && event.textValue().equals("error")){
            super.handleError(message, new ExchangeException("Error code: " + message.get("error_code").asText()));

        }
        if (message.get("data") != null) {
            super.handleMessage(message);
        }
    }
}
