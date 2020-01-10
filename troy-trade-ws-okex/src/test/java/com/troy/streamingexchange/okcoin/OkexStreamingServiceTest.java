package com.troy.streamingexchange.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.okex.OkexStreamingService;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class OkexStreamingServiceTest {

    private OkexStreamingService streamingService;

    @Before("setUp")
    public void setUp() throws Exception {
        streamingService = new OkexStreamingService("wss://example.com/websocket");
    }

    @Test
    public void testGetSubscribeMessage() throws Exception {
        String subscribeMessage = streamingService.getSubscribeMessage("ok_sub_spot_btc_usd_depth");
        String expected = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("subscribe.json").toURI())));
        assertThat(subscribeMessage).isEqualTo(expected);
    }

    @Test
    public void testGetUnsubscribeMessage() throws Exception {
        String subscribeMessage = streamingService.getUnsubscribeMessage("orderbook");
        String expected = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("unsubscribe.json").toURI())));
        assertThat(subscribeMessage).isEqualTo(expected);
    }

    @Test
    public void testGetChannelFromMessage() throws Exception {
        String expected = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("order-book.json").toURI())));
        JsonNode data = new ObjectMapper().readTree(expected);
        String channel = streamingService.getChannelNameFromMessage(data);

        assertThat(channel).isEqualTo("ok_sub_spot_btc_usd_depth");
    }
}