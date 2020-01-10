package com.troy.streamingexchange.okcoin;

import com.troy.streamingexchange.okex.OkexStreamingExchange;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkexManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(OkexManualExample.class);

    public static void main(String[] args) {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(OkexStreamingExchange.class.getName());
        exchange.connect().blockingAwait();

        CurrencyPair btcUsdt = new CurrencyPair("BTC", "USDT");
        exchange.getStreamingMarketDataService().getOrderBook(btcUsdt).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks().get(0));
            LOG.info("First bid: {}", orderBook.getBids().get(0));
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

//        exchange.getStreamingMarketDataService().getTicker(btcUsdt).subscribe(ticker -> {
//            LOG.info("TICKER: {}", ticker);
//        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
//
//        exchange.getStreamingMarketDataService().getTrades(btcUsdt).subscribe(trade -> {
//            LOG.info("TRADE: {}", trade);
//        }, throwable -> LOG.error("ERROR in getting trades: ", throwable));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
