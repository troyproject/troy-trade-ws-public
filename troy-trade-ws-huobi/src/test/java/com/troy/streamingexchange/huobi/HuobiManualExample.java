package com.troy.streamingexchange.huobi;

import com.troy.trade.ws.dto.LimitOrder;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class HuobiManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(HuobiManualExample.class);

    public static void main(String[] args) throws InterruptedException {
        StreamingExchange exchange;
        try {
            exchange = StreamingExchangeFactory.INSTANCE.createExchange(HuobiProStreamingExchange.class
                    .getName());
            exchange.connect().blockingAwait();
        } catch (Exception e) {
            LOG.error("================= connection exception================= :{}", e.getMessage());
            return;
        }
//        Disposable tickerDisposable = exchange.getStreamingMarketDataService().getTicker(new CurrencyPair("MEET/ETH")).subscribe(ticker -> {
//            LOG.info(" ================= ticker =================: {}", ticker);
//        }, throwable -> {
//            LOG.error("ERROR in getting ticker: ", throwable);
//        });
        Thread.sleep(2000);
//        tickerDisposable.dispose();

        CurrencyPair currencyPair = new CurrencyPair("BTC","USDT");
        Disposable depthDisposable = exchange.getStreamingMarketDataService().getOrderBook(currencyPair, "step0").subscribe(orderBook -> {
            List<LimitOrder> asks = orderBook.getAsks();
            List<LimitOrder> bids = orderBook.getBids();
            LOG.info("卖2: {}-{}", asks.get(asks.size() - 2).getLimitPrice(), asks.get(asks.size() - 2).getOriginalAmount());
            LOG.info("卖1: {}-{}", asks.get(asks.size() - 1).getLimitPrice(), asks.get(asks.size() - 1).getOriginalAmount());
            LOG.info("买1: {}-{}", bids.get(0).getLimitPrice(), bids.get(0).getOriginalAmount());
            LOG.info("买2: {}-{}", bids.get(1).getLimitPrice(), bids.get(1).getOriginalAmount());
            LOG.info(" ================= depth 记录数 =================ask:{}- bids:{}", orderBook.getAsks().size(), orderBook.getBids().size());

        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));
        Thread.sleep(2000);
        depthDisposable.dispose();
//
//        Disposable tradeDisposable = exchange.getStreamingMarketDataService().getTrades(new CurrencyPair("MEET/ETH")).subscribe(trades -> {
//            LOG.info(" ================= trade 记录数 =================: {}", trades.size());
//        }, throwable -> LOG.error("ERROR in getting trade: ", throwable));
//        Thread.sleep(2000);
////        tradeDisposable.dispose();
//
//        Disposable tradeReqDisposable = exchange.getStreamingMarketDataService().getTradesOnce(new CurrencyPair("MEET/ETH")).subscribe(trades -> {
//            LOG.info(" ================= tradeReq 记录数 =================: {}", trades.size());
//        }, throwable -> LOG.error("ERROR in req trade: ", throwable));
//        Thread.sleep(2000);
//        tradeReqDisposable.dispose();
    }
}
