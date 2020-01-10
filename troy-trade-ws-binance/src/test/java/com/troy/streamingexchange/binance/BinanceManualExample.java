package com.troy.streamingexchange.binance;

import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Lukas Zaoralek on 15.11.17.
 */
public class BinanceManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceManualExample.class);

    public static void main(String[] args) {

        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(BinanceStreamingExchange.class.getName());

        CurrencyPair currencyPair = new CurrencyPair("BTC/USDT");
        ProductSubscription subscription = ProductSubscription.create()

                .addTicker(currencyPair)
                .addOrderbook(currencyPair)
                .addTrades(currencyPair)
                .build();

        exchange.connect(subscription).blockingAwait();

//        exchange.getStreamingMarketDataService()
//                .getOrderBook(currencyPair)
//                .subscribe(orderBook -> {
//                    LOG.info("1、Order Book: {}", orderBook);
//                }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

//        exchange.getStreamingMarketDataService()
//                .getOrderBook(currencyPair)
//                .subscribe(orderBook -> {
//                    LOG.info("2、Order Book: {}", orderBook);
//                }, throwable -> LOG.error("ERROR in getting order book: ", throwable));


//        exchange.getStreamingMarketDataService()
//                .getTicker(currencyPair)
//                .subscribe(ticker -> {
//                    LOG.info("Ticker: {}", ticker);
//                }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));

//        CurrencyPair ltcBtc = new CurrencyPair("LTC/BTC");
//        exchange.getStreamingMarketDataService()
//                .getOrderBook(ltcBtc)
//                .subscribe(orderBook -> {
//                    LOG.info("Order Book: {}", orderBook);
//                }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        exchange.getStreamingMarketDataService()
                .getTrades(currencyPair)
                .subscribe(trade -> {
                    LOG.info("Trade: {}", trade);
                });
    }
}
