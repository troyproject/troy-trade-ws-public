package com.troy.streamingexchange.huobi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.troy.streamingexchange.huobi.dto.HuobiWebSocketUpdateOrderbook;

import java.io.IOException;

public class HuobiWebSocketUpdateOrderbookDeserializer extends StdDeserializer<HuobiWebSocketUpdateOrderbook> {
    protected HuobiWebSocketUpdateOrderbookDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public HuobiWebSocketUpdateOrderbook deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return null;
    }
}
