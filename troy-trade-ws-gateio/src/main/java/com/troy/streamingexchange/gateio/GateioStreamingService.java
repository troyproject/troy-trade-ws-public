package com.troy.streamingexchange.gateio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.Lists;
import com.troy.streamingexchange.gateio.dto.GateioWebSocketSubscriptionMessage;
import com.troy.streamingexchange.gateio.dto.GateioWebsocketTypes;
import com.troy.streamingexchange.gateio.service.exception.GateioException;
import com.troy.streamingexchange.gateio.service.netty.AbstractJsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.troy.streamingexchange.gateio.dto.GateioWebsocketTypes.*;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioStreamingService extends AbstractJsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(GateioStreamingService.class);

    private static final String JSON_METHOD = "method";
    private static final String JSON_PARAMS = "params";
    private static final String JSON_RESULT = "result";
    private static final String JSON_RESULT_STATUS = "status";
    private static final String JSON_ERROR = "error";
    private static final String JSON_ID = "id";


    /**
     * Map request Id to Chanel Name and Gateio method pair
     */
    private final Map<Integer, Pair<String, String>> requests = new HashMap<>();
    private final ObjectMapper objectMapper;


    public GateioStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {

        if (message.has(JSON_ID)) {
            int requestId = message.get(JSON_ID).asInt();
            if (requests.containsKey(requestId)) {
                return requests.get(requestId).getKey();
            }
        }

        if (message.has(JSON_METHOD)) {
            String method = message.get(JSON_METHOD).asText();
            if (message.has(JSON_PARAMS)) {
                String symbol = "";
                if (GateioWebsocketTypes.KLINE_UPDATE.getSerializedValue().equals(method)) {
                    Iterator<JsonNode> iterator = message.get(JSON_PARAMS).get(0).iterator();
                    while (iterator.hasNext()) {
                        JsonNode node = iterator.next();
                        if (!iterator.hasNext()) {
                            symbol = node.asText();
                        }
                    }
                } else if (GateioWebsocketTypes.DEPTH_UPDATE.getSerializedValue().equals(method)) {
                    symbol = message.get(JSON_PARAMS).get(2).asText();
                } else if (GateioWebsocketTypes.ORDER_UPDATE.getSerializedValue().equals(method)) {
                    symbol = message.get(JSON_PARAMS).get(1).get("market").asText();
                } else {
                    symbol = message.get(JSON_PARAMS).get(0).asText();
                }
                return method.split("\\.")[0] + "-" + symbol;
            }
            return method;
        }

        throw new IOException("Channel name can't be evaluated from message");
    }

    @Override
    protected void handleMessage(JsonNode message) {
        //如果是订阅请求|ping|签名
        if (message.has(JSON_ID)) {
            int requestId = message.get(JSON_ID).asInt();
            if (requests.containsKey(requestId)) {

                String subscriptionMethod = requests.get(requestId).getLeft();
                //error为null时表示success
                if (message.has(JSON_ERROR) && !(message.get(JSON_ERROR) instanceof NullNode)) {
                    try {
                        GateioException exception = objectMapper.treeToValue(message, GateioException.class);
                        super.handleError(message, exception);
                    } catch (JsonProcessingException e) {
                        super.handleError(message, e);
                    }
                } else {
                    JsonNode jsonResult = message.get(JSON_RESULT);
                    if (jsonResult.asText().equals("pong")) {
                        LOG.info("Gateio returned {} as result of '{}' method", "pong", subscriptionMethod);
                        requests.remove(requestId);
                    }
                    if (jsonResult.get(JSON_RESULT_STATUS) != null) {
                        boolean result = "success".equals(jsonResult.get(JSON_RESULT_STATUS).asText());
                        LOG.info("Gateio returned {} as result of '{}' method", result, subscriptionMethod);
                    }
                }
                requests.remove(requestId);
                if (subscriptionMethod.contains(GateioWebsocketTypes.SERVER_SIGN.getSerializedValue())) {
                    super.handleChannelMessage(subscriptionMethod, message);
                }
                return;
            }
        }

        String channel = getChannel(message);
        LOG.info("handleMessage getChannel:{}", channel);
        if (!channels.containsKey(channel)) {
            LOG.warn("The message has been received from disconnected channel '{}'. Skipped.", channel);
            return;
        }

        super.handleMessage(message);
    }


    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        GateioWebSocketSubscriptionMessage subscribeMessage = generateSubscribeMessage(channelName, "subscribe", args);
        requests.put(subscribeMessage.getId(), ImmutablePair.of(channelName, subscribeMessage.getMethod()));

        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {

        GateioWebSocketSubscriptionMessage subscribeMessage = generateSubscribeMessage(channelName, "unsubscribe");
        requests.put(subscribeMessage.getId(), ImmutablePair.of(channelName, subscribeMessage.getMethod()));

        return objectMapper.writeValueAsString(subscribeMessage);

    }

    /**
     * @param channelName k线: kline-86400-ETH_BTC 盘口:depth-ETH_BTC
     * @param methodType
     * @param args
     * @return
     * @throws IOException
     */
    private GateioWebSocketSubscriptionMessage generateSubscribeMessage(String channelName, String methodType, Object... args) throws IOException {

        String[] chanelInfo = channelName.split("-");

        String websocketType = chanelInfo[0];
        GateioWebsocketTypes types = GateioWebsocketTypes.fromTransactionValue(websocketType);

        if (chanelInfo.length < 2 && SERVER_PING.compareTo(types) != 0 && SERVER_TIME.compareTo(types) != 0) {
            throw new IllegalArgumentException(methodType + " message: channel name must has format <channelName>-<Symbol> (e.g orderbook-ETH_BTC)");
        }

        String method = chanelInfo[0] + "." + methodType;

        List params = Lists.newArrayList();
        //比如kline-86400-ETH_BTC
        if (KLINE.compareTo(types) == 0) {
            if (args != null && args.length == 1) {
                String symbol = chanelInfo[1];
                String period = args[0].toString();
                params = Lists.newArrayList(symbol, Integer.valueOf(period).intValue());
            }
        } else if (DEPTH.compareTo(types) == 0) {
            String symbol = chanelInfo[1];
            if (args != null && args.length == 2) {
                Integer limit = Integer.valueOf(args[0].toString());
                String interval = args[1].toString();
                params = Lists.newArrayList(symbol, limit, interval);
            }
        } else if (SERVER_SIGN.compareTo(types) == 0) {
            method = SERVER_SIGN.getSerializedValue();
            if (args != null && args.length == 3) {
                params = Lists.newArrayList(args[0], args[1], args[2]);
            }
        } else if (SERVER_PING.compareTo(types) == 0) {
            method = SERVER_PING.getSerializedValue();
        } else if (SERVER_TIME.compareTo(types) == 0) {
            method = SERVER_TIME.getSerializedValue();
        } else {
            String symbol = chanelInfo[1];
            params = Lists.newArrayList(symbol);
        }

        int requestId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

        return new GateioWebSocketSubscriptionMessage(requestId, method, params);
    }
}
