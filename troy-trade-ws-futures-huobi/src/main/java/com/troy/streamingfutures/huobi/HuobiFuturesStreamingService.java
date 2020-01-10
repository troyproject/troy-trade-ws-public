package com.troy.streamingfutures.huobi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingfutures.huobi.dto.HuobiFuturesPongMessage;
import com.troy.streamingfutures.huobi.dto.message.HuobiFuturesMessage;
import com.troy.streamingfutures.huobi.dto.message.HuobiFuturesRequestMessage;
import com.troy.streamingfutures.huobi.dto.message.HuobiFuturesSubscriptionMessage;
import com.troy.streamingfutures.huobi.dto.message.HuobiFuturesUnSubscriptionMessage;
import com.troy.trade.ws.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class HuobiFuturesStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(HuobiFuturesStreamingService.class);

    private static final String OK = "ok";
    private static final String ERROR = "error";
    private static final String SUBSCRIBED = "subbed";
    private static final String REP = "rep";
    private static final String UNSUBSCRIBED = "unsubbed";
    private static final String CHANNEL_ID = "ch";

    public HuobiFuturesStreamingService(String apiUrl) {
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
            HuobiFuturesPongMessage huobiPongMessage = new HuobiFuturesPongMessage(Long.valueOf(hb));
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                sendMessage(objectMapper.writeValueAsString(huobiPongMessage));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return;
        }

        JsonNode status = message.get("status");
        if (status == null) {
            super.handleMessage(message);
        }else{
            if (status.textValue().equals(OK)) {
                Boolean rep = message.has(REP);
                /**
                 * 如果是websocket请求返回的数据,
                 * 直接发射数据,然后从通道中删除,避免
                 */
                if (rep) {
                    //eg market.$symbol.trade.detail
                    try {
                        String channelId = this.getChannelNameFromMessage(message);
                        super.handleChannelMessage(channelId, message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else if (status.textValue().equals(ERROR)) {//订阅错误
                LOG.error("===================recieve error==============:{}", message.get("err-msg").asText());
            }
            LOG.debug("订阅火币合约返回内容:{}", message.asText());
        }
    }

    /**
     *
     * @param channelName
     * @param args 第一个是交易对名称，如：BTC_CW,第二个是Depth 类型，如：step0
     * @return
     */
    @Override
    public String getSubscriptionUniqueId(String channelName, Object... args) {
        return String.format(channelName,args);
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
        return chanId;
    }

    /**
     * 获取订阅信息
     * @param channelName
     * @param args
     * @return
     * @throws IOException
     */
    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        String requestId = String.valueOf(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));

        HuobiFuturesMessage subscribeMessage = null;

        //订阅
        String sub = String.format(channelName,args);
        subscribeMessage =
                new HuobiFuturesSubscriptionMessage(sub, requestId);
        if (subscribeMessage == null) throw new IOException("SubscribeMessage: Insufficient arguments");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelId) throws IOException {
        if (channelId == null) throw new IOException("Can't find channel unique name");
        String requestId = String.valueOf(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        HuobiFuturesUnSubscriptionMessage subscribeMessage =
                new HuobiFuturesUnSubscriptionMessage(channelId, requestId);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(subscribeMessage);
    }


}
