package com.troy.streamingexchange.bitfinex;

import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Lukas Zaoralek on 7.11.17.
 */
public class BitfinexManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(BitfinexManualExample.class);

    public static void main(String[] args) {

        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class
                .getName());
        exchange.connect().blockingAwait();

        CurrencyPair currencyPair = new CurrencyPair("BTC","USD");
        exchange.getStreamingMarketDataService().getOrderBook(currencyPair).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks());
            LOG.info("First bid: {}", orderBook.getBids());
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

//        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_EUR).subscribe(ticker -> {
//            LOG.info("TICKER: {}", ticker);
//        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));

//        exchange.getStreamingMarketDataService().getTrades(currencyPair)
//                .subscribe(trade -> {
//                    LOG.info("TRADE: {}", trade);
//                }, throwable -> LOG.error("ERROR in getting trade: ", throwable));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
