package com.troy.streamingexchange.gateio;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.troy.streamingexchange.gateio.dto.GateioOrderUpdate;
import com.troy.streamingexchange.gateio.dto.GateioWebSocketOpenOrderParams;
import com.troy.streamingexchange.gateio.dto.GateioWebSocketOpenOrderTranscation;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * GateioStreamingOrderServiceTest
 *
 * @author liuxiaocheng
 * @date 2018/7/2
 */
@RunWith(MockitoJUnitRunner.class)

public class GateioStreamingOrderServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Mock
    private GateioStreamingService streamingService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testOpenorders() throws Exception {

        // Read order update in JSON
        String orderUpdate = IOUtils.toString(getClass().getResource("/json/notificationOrder.json"), "UTF8");
//        when(streamingService.subscribeChannel(eq("orderbook-BTCEUR"))).thenReturn(Observable.just(objectMapper.readTree(orderUpdate)));

//        GateioWebSocketOpenOrderTranscation o = toGateioWebSocketOrderTransaction(orderUpdate);
//        System.out.println(JSONObject.toJSONString(o));



        StreamingExchange gateioStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(GateioStreamingExchange.class
                .getName());
        gateioStreamingExchange.connect().blockingAwait();

        Integer limit = 10;
        BigDecimal interval = new BigDecimal(0.00001);

        CurrencyPair currencyPair = new CurrencyPair("BTC/USDT");
        gateioStreamingExchange.getStreamingMarketDataService().getOrderBook(currencyPair, new Object[]{limit, interval}).subscribe
                (
                        orderBook -> {
                            logger.info(""+JSONObject.toJSONString(orderBook));
                        },
                        throwable -> logger.error("ERROR in getting depth: ", throwable)
                );
    }
    private GateioWebSocketOpenOrderTranscation toGateioWebSocketOrderTransaction(String s) {
        List<GateioOrderUpdate> orders = Lists.newArrayList();
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray("params");
        if (jsonArray != null && jsonArray.toArray().length > 0) {
            for (int i = 0; i < jsonArray.toArray().length; i = i + 2) {
                GateioOrderUpdate gateioOrderUpdate = new GateioOrderUpdate();
                Integer eventType;
                if (i % 2 == 0) {
                    eventType = jsonArray.getInteger(i);
                    gateioOrderUpdate.setEventType(eventType);
                }
                JSONObject orderUpdate = (JSONObject) jsonArray.get(i + 1);
                gateioOrderUpdate.setOrderId(orderUpdate.getString("id"));
                String[] market = orderUpdate.getString("market").split("_");//交易对
                gateioOrderUpdate.setCurrencyPair(new CurrencyPair(market[0], market[1]));
                gateioOrderUpdate.setOrderType(orderUpdate.getString("orderType"));
                gateioOrderUpdate.setType(orderUpdate.getString("type"));
                gateioOrderUpdate.setCtime(orderUpdate.getLong("ctime"));
                gateioOrderUpdate.setPrice(orderUpdate.getBigDecimal("price"));
                gateioOrderUpdate.setAmount(orderUpdate.getBigDecimal("amount"));
                gateioOrderUpdate.setLeft(orderUpdate.getBigDecimal("left"));
                gateioOrderUpdate.setFilledAmount(orderUpdate.getBigDecimal("filledAmount"));
                gateioOrderUpdate.setFilledTotal(orderUpdate.getBigDecimal("filledTotal"));
                gateioOrderUpdate.setDealFee(orderUpdate.getBigDecimal("dealFee"));
                orders.add(gateioOrderUpdate);
            }
        }
        return new GateioWebSocketOpenOrderTranscation(null, new GateioWebSocketOpenOrderParams(null, orders));
    }
}
