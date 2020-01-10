package com.troy.streamingexchange.gateio;

import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Lukas Zaoralek on 7.11.17.
 */
public class GateioManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(GateioManualExample.class);
    private static final String apiKey = "9FE14C26-C3E8-45ED-871D-DE94DCF3759F";
    private static final String secretKey = "a91f9b51f5772ee3a36b39695b503d20ee5f64fefccf468db8e060636a05cea4";

    public static void main(String[] args) {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(GateioStreamingExchange.class
                .getName());

        exchange.connect().blockingAwait();

        CurrencyPair currencyPair = new CurrencyPair("BTC","USDT");
//
//        Disposable tickerObserver = exchange.getStreamingMarketDataService().getTicker(CurrencyPair.ETH_BTC).subscribe(ticker -> {
//            LOG.info("getTicker TICKER: {}", ticker);
//        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
//
//
        Disposable tradeObserver = exchange.getStreamingMarketDataService().getTrades(currencyPair).subscribe(trades -> {
            LOG.info("getTrades TRADE: {}", trades);
        }, throwable -> LOG.error("ERROR in getting trade: ", throwable));
//
//        Disposable klineObserver = ((GateioStreamingMarketDataService) exchange.getStreamingMarketDataService()).getKline(CurrencyPair.BTC_USDT, KlineInterval.m1).subscribe(ticker -> {
//            LOG.info("getKline TICKER: {}", ticker);
//        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));


//        Disposable depthObserver = exchange.getStreamingMarketDataService().getOrderBook(currencyPair, new Object[]{30, "0"}).subscribe(orderBook -> {
//            LOG.info("getOrderBook ORDERBOOK getAsks: {},asks:{}", orderBook.getAsks().size(),orderBook.getAsks());
//            LOG.info("getOrderBook ORDERBOOK getBids: {},bids:{}", orderBook.getBids().size(),orderBook.getBids());
//        }, throwable -> LOG.error("ERROR in getting trade: ", throwable));

//        Disposable authDisposable = ((GateioStreamingMarketDataService) exchange.getStreamingMarketDataService()).authenticated(apiKey, secretKey).subscribe(
//                resutl -> {
//                    LOG.info("authenticated : {}", resutl);
//                    Disposable openOrderObserver = ((GateioStreamingMarketDataService) exchange.getStreamingMarketDataService()).getOpenOrders(new CurrencyPair("TRX", "USDT"), apiKey, secretKey).subscribe(
//                            openOrders -> LOG.info("openOrderObserver OPENORDERS: {}", openOrders),
//                            throwable -> LOG.error("ERROR in getting OPENORDERS: ", throwable)
//                    );
//                },
//                throwable -> LOG.error("ERROR in authenticated : ", throwable)
//        );
//        Disposable pingDisposable = Observable.interval(5, TimeUnit.SECONDS).subscribe(tick -> {
//            ((GateioStreamingMarketDataService) exchange.getStreamingMarketDataService()).ping();
//            LOG.info("==============tick:{}=============", tick);
//        });
//        pingDisposable.dispose();
////      tickerObserver.dispose();
//        klineObserver.dispose();
//        tradeObserver.dispose();
//        depthObserver.dispose();
//        tradeOrderObserver.dispose();
//        exchange.disconnect().subscribe(() -> LOG.info("Disconnected"));

    }
}
