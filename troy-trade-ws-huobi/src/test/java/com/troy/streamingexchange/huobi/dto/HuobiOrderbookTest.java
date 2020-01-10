package com.troy.streamingexchange.huobi.dto;

import com.alibaba.fastjson.JSONObject;
import com.troy.streamingexchange.huobi.HuobiProStreamingExchange;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import org.junit.Test;

public class HuobiOrderbookTest {

    @Test
    public void timestampShouldBeInSeconds() {
        HuobiOrderbook depth = new HuobiOrderbook();

//        OrderBook orderBook = HuobiAdapters.adaptOrderBook(depth, BTC_USD);
//
//        // What is the time now... after order books created?
//        assertThat("The timestamp should be a value less than now, but was: " + orderBook.getTimeStamp(),
//                !orderBook.getTimeStamp().after(new Date()));


//        StreamingExchange huobiStreamingExchange= StreamingExchangeFactory.INSTANCE.createExchange(HuobiStreamingExchange.class
//                .getName());
//        huobiStreamingExchange.connect().blockingAwait();
//
//        Integer limit = 100;
////        BigDecimal interval = new BigDecimal(0.00001);
//
//        huobiStreamingExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USDT, new Object[]{limit, interval}).subscribe
//                (
//                        orderBook -> {
//                            System.out.println(""+ JSONObject.toJSONString(orderBook));
//                        },
//                        throwable -> System.out.println("ERROR in getting depth: "+throwable.getLocalizedMessage())
//                );

        String exchangeClassName = HuobiProStreamingExchange.class.getName();
        StreamingExchange huoioStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeClassName);
        huoioStreamingExchange.connect().blockingAwait();

        String interval = "step0";
        if (huoioStreamingExchange != null && huoioStreamingExchange.getStreamingMarketDataService() != null) {
            huoioStreamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair("BTC/USDT"), interval).subscribe
                    (
                            orderBook -> {
                                System.out.println(""+ JSONObject.toJSONString(orderBook));
//                                notificationService.broadcastDepth(key, orderBook, exchange, depthSubscribeRequestBody.getParams().getInterval(), limit, currencyPair,false);
//                                notificationService.broadcastDepthChart(key, orderBook, exchange, depthSubscribeRequestBody.getParams().getInterval(), limit, currencyPair);
                            },
                            throwable -> System.out.println("ERROR in getting depth: "+ throwable.getLocalizedMessage())
                    );
        }


    }
}