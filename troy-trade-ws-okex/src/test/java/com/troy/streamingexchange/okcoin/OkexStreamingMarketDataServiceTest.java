package com.troy.streamingexchange.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.okex.OkexStreamingMarketDataService;
import com.troy.streamingexchange.okex.OkexStreamingService;
import com.troy.trade.ws.dto.LimitOrder;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import static org.mockito.ArgumentMatchers.any;

public class OkexStreamingMarketDataServiceTest {

    @Mock
    private OkexStreamingService okexStreamingService;
    private OkexStreamingMarketDataService marketDataService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        marketDataService = new OkexStreamingMarketDataService(okexStreamingService);
    }

    @Test
    public void testGetOrderBook() throws Exception {
        // Given order book in JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader().getResourceAsStream("order-book.json"));

//        when(okCoinStreamingService.subscribeChannel(any())).thenReturn(Observable.just(jsonNode));

        Date timestamp = new Date(1484602135246L);
        CurrencyPair currencyPair = new CurrencyPair("BTC","USD");

        List<LimitOrder> bids = new ArrayList<>();
        bids.add(new LimitOrder(OrderTypeEnum.BID, new BigDecimal("0.922"), currencyPair, null, timestamp, new BigDecimal("819.9")));
        bids.add(new LimitOrder(OrderTypeEnum.BID, new BigDecimal("0.085"), currencyPair, null, timestamp, new BigDecimal("818.63")));

        List<LimitOrder> asks = new ArrayList<>();
        asks.add(new LimitOrder(OrderTypeEnum.ASK, new BigDecimal("0.035"), currencyPair, null, timestamp, new BigDecimal("821.6")));
        asks.add(new LimitOrder(OrderTypeEnum.ASK, new BigDecimal("5.18"), currencyPair, null, timestamp, new BigDecimal("821.65")));
        asks.add(new LimitOrder(OrderTypeEnum.ASK, new BigDecimal("2.89"), currencyPair, null, timestamp, new BigDecimal("821.7")));

        OrderBook expected = new OrderBook(timestamp, asks, bids);

        // Call get order book observable
        TestObserver<OrderBook> test = marketDataService.getOrderBook(currencyPair).test();

        // Get order book object in correct order
        test.assertResult(expected);
    }
}
