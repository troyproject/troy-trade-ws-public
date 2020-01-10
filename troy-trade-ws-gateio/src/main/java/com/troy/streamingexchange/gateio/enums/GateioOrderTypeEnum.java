package com.troy.streamingexchange.gateio.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = GateioOrderTypeEnum.BTEROrderTypeDeserializer.class)
public enum GateioOrderTypeEnum {
    BUY,
    SELL;

    static class BTEROrderTypeDeserializer extends JsonDeserializer<GateioOrderTypeEnum> {

        @Override
        public GateioOrderTypeEnum deserialize(JsonParser jsonParser, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            final ObjectCodec oc = jsonParser.getCodec();
            final JsonNode node = oc.readTree(jsonParser);
            final String orderType = node.asText();
            return GateioOrderTypeEnum.valueOf(orderType.toUpperCase());
        }
    }
}
