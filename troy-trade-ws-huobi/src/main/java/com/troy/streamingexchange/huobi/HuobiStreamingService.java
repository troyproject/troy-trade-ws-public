package com.troy.streamingexchange.huobi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.huobi.dto.HuobiPongMessage;
import com.troy.streamingexchange.huobi.dto.message.HuobiWebSocketMessage;
import com.troy.streamingexchange.huobi.dto.message.HuobiWebSocketRequestMessage;
import com.troy.streamingexchange.huobi.dto.message.HuobiWebSocketSubscriptionMessage;
import com.troy.streamingexchange.huobi.dto.message.HuobiWebSocketUnSubscriptionMessage;
import com.troy.trade.ws.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class HuobiStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(HuobiStreamingService.class);

    private static final String OK = "ok";
    private static final String ERROR = "error";
    private static final String SUBSCRIBED = "subbed";
    private static final String REP = "rep";
    private static final String UNSUBSCRIBED = "unsubbed";
    private static final String CHANNEL_ID = "ch";
    /**
     * 订阅的渠道
     * key-value  key:第三方推送的消息中的ch字段值, value: getSubscriptionUniqueId()返回的唯一id
     * eg: "market.ethusdt.depth.step0"->"depth-ethusdt"
     */
    private final Map<String, String> subscribedChannels = new HashMap<>();

    public HuobiStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
    }

    @Override
    public void messageHandler(String message) {
        LOG.debug("Received message: {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        // Parse incoming message to JSON
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (IOException e) {
            LOG.error("Error parsing incoming message to JSON: {}", message);
            return;
        }

        handleMessage(jsonNode);
    }

    /**
     * @param message 消息格式
     *                {
     *                "sub": "market.btcusdt.kline.1min",
     *                "id": "id1"
     *                }
     *                {
     *                "unsub": "market.btcusdt.trade.detail",
     *                "id": "id4"
     *                }
     */
    @Override
    protected void handleMessage(JsonNode message) {
        //Heart beating
        if (message.has("ping")) {
            String hb = message.get("ping").asText();
            LOG.debug("Heart beating内容:{}", hb);
            HuobiPongMessage huobiPongMessage = new HuobiPongMessage(Long.valueOf(hb));
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                sendMessage(objectMapper.writeValueAsString(huobiPongMessage));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return;
        }

        JsonNode status = message.get("status");
        if (status != null) {
            if (status.textValue().equals(OK)) {
                Boolean subbed = message.has(SUBSCRIBED);
                Boolean unsubbed = message.has(UNSUBSCRIBED);
                Boolean rep = message.has(REP);
                /**
                 * 如果是websocket请求返回的数据,
                 * 直接发射数据,然后从通道中删除,避免
                 */
                if (rep) {
                    //eg market.$symbol.trade.detail
                    String reqMessage = message.get(REP).asText();
                    String pair = reqMessage.split("\\.")[1];
                    String channelName = reqMessage.split("\\.")[0] + "." + reqMessage.split("\\.")[2] + "_" + "req";
                    String channelId = getSubscriptionUniqueId(channelName, pair);
                    super.handleChannelMessage(channelId, message);
                } else {
                    /**
                     * 如果是订阅返回
                     */
                    if (subbed) {
                        String subbedMessage = message.get(SUBSCRIBED).asText();
                        String pair = subbedMessage.split("\\.")[1];
                        String channelName = subbedMessage.split("\\.")[0] + "." + subbedMessage.split("\\.")[2];
                        String channelId = message.get(SUBSCRIBED).asText();
                        try {
                            String subscriptionUniqueId = getSubscriptionUniqueId(channelName, pair);
                            subscribedChannels.put(channelId, subscriptionUniqueId);
                            LOG.debug("Register channel subscriptionUniqueId :[{}],channelId :[{}]", subscriptionUniqueId, channelId);
                        } catch (Exception e) {
                            LOG.error(e.getMessage());
                        }
                    } else if (unsubbed) {
                        String channelId = message.get(UNSUBSCRIBED).asText();
                        subscribedChannels.remove(channelId);
                    }
                }

            } else if (status.textValue().equals(ERROR)) {//订阅错误
                LOG.error("===================recieve error==============:{}", message.get("err-msg").asText());
            }
        } else super.handleMessage(message);
    }

    @Override
    public String getSubscriptionUniqueId(String channelName, Object... args) {
        return channelName + "-" + args[0].toString();
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        String chanId;
        if (message.has(CHANNEL_ID)) {
            chanId = message.get(CHANNEL_ID).asText();
        } else {
            chanId = message.get(0).asText();
        }

        if (chanId == null) throw new IOException("Can't find CHANNEL_ID value");
        return subscribedChannels.get(chanId);
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        String requestId = String.valueOf(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));

        HuobiWebSocketMessage subscribeMessage = null;
        if (TopicType.TOPIC_MARKET_DEPTH.equals(channelName)) {
            if (args.length == 2) {
                subscribeMessage =
                        new HuobiWebSocketSubscriptionMessage(channelName, requestId, args[0].toString(), null, args[1].toString());
            }
        }
        if (TopicType.TOPIC_MARKET_TRADE.equals(channelName) || TopicType.TOPIC_MARKET_DETAIL.equals(channelName)) {
            subscribeMessage =
                    new HuobiWebSocketSubscriptionMessage(channelName, requestId, args[0].toString(), null, null);
        }
        if (TopicType.TOPIC_MARKET_TRADE_REQ.equals(channelName)) {
            subscribeMessage = new HuobiWebSocketRequestMessage(requestId, args[0].toString());
        }
        if (subscribeMessage == null) throw new IOException("SubscribeMessage: Insufficient arguments");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        String channelId = null;
        for (Map.Entry<String, String> entry : subscribedChannels.entrySet()) {
            if (entry.getValue().equals(channelName)) {
                channelId = entry.getKey();
                break;
            }
        }
        if (channelId == null) throw new IOException("Can't find channel unique name");
        String requestId = String.valueOf(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));

        HuobiWebSocketUnSubscriptionMessage subscribeMessage =
                new HuobiWebSocketUnSubscriptionMessage(channelId, requestId);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(subscribeMessage);
    }
}
