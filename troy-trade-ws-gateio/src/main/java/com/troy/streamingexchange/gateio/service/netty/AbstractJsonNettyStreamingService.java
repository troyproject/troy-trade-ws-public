package com.troy.streamingexchange.gateio.service.netty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractJsonNettyStreamingService extends AbstractNettyStreamingService<JsonNode> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonNettyStreamingService.class);

    public AbstractJsonNettyStreamingService(String apiUrl) {
        super(apiUrl);
    }

    public AbstractJsonNettyStreamingService(String apiUrl, int maxFramePayloadLength) {
        super(apiUrl, maxFramePayloadLength);
    }

    @Override
    public void messageHandler(String message) {
        LOG.debug("Received message: {}", message);

        if(message.contains("\"pong\"")){//{"error":null,"result":"pong","id":825274903}
//            LOG.info("心跳返回数据，不做处理");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        // Parse incoming message to JSON
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (IOException e) {
            LOG.error("Error parsing incoming message to JSON: {}", message);
            return;
        }

        // In case of array - handle every message separately.
        if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            for (JsonNode node : jsonNode) {
                handleMessage(node);
            }
        } else {
            handleMessage(jsonNode);
        }
    }
}