package com.troy.streamingexchange.binance;

import com.fasterxml.jackson.databind.JsonNode;
import com.troy.trade.ws.netty.JsonNettyStreamingService;
import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BinanceStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceStreamingService.class);

    private ProductSubscription productSubscription;

    public BinanceStreamingService(String baseUri, ProductSubscription productSubscription) {
        super(baseUri, Integer.MAX_VALUE);
        this.productSubscription = productSubscription;
    }

    @Override
    public void messageHandler(String message) {
        super.messageHandler(message);
    }

    @Override
    protected void handleMessage(JsonNode message) {
        super.handleMessage(message);
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        return message.get("stream").asText();
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        // No op. Disconnecting from the web socket will cancel subscriptions.
        return null;
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        // No op. Disconnecting from the web socket will cancel subscriptions.
        return null;
    }

    @Override
    public void sendMessage(String message) {
        // Subscriptions are made upon connection - no messages are sent.
    }

    /**
     * The available subscriptions for this streaming service.
     * @return The subscriptions for the currently open connection.
     */
    public ProductSubscription getProductSubscription() {
        return productSubscription;
    }


}
