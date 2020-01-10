package com.troy.streamingfutures.okex;

import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkexFuturesManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(OkexFuturesManualExample.class);

    public static void main(String[] args) {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(OkexFuturesStreamingExchange.class.getName());
        exchange.connect().blockingAwait();

        CurrencyPair currencyPair = new CurrencyPair("BTC/USD");
        exchange.getStreamingMarketDataService().getOrderBook(currencyPair, false,"BTC-USD-200327").subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks());
            LOG.info("First bid: {}", orderBook.getBids());
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

//        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_USD, AliasEnum.Quarter).subscribe(ticker -> {
//            LOG.info("TICKER: {}", ticker);
//        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
//
//        exchange.getStreamingMarketDataService().getTrades(currencyPair, "BTC-USD-200327").subscribe(trade -> {
//            LOG.info("TRADE: {}", trade);
//        }, throwable -> LOG.error("ERROR in getting trades: ", throwable));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

