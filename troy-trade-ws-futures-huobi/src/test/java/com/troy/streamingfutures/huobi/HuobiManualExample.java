package com.troy.streamingfutures.huobi;

import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.trade.ws.dto.LimitOrder;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class HuobiManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(HuobiManualExample.class);

    public static void main(String[] args) throws InterruptedException {
        StreamingExchange exchange;
        try {
            exchange = StreamingExchangeFactory.INSTANCE.createExchange(HuobiFuturesStreamingExchange.class
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
//        Thread.sleep(2000);
//        tickerDisposable.dispose();

//        CurrencyPair currencyPair = new CurrencyPair("BTC","USDT");
//        AliasEnum aliasEnum = AliasEnum.THIS_WEEK;
//        Disposable depthDisposable = exchange.getStreamingMarketDataService().getOrderBook(currencyPair, aliasEnum,"step0").subscribe(orderBook -> {
//            List<LimitOrder> asks = orderBook.getAsks();
//            List<LimitOrder> bids = orderBook.getBids();
//            LOG.info("卖2: {}-{}", asks.get(asks.size() - 2).getLimitPrice(), asks.get(asks.size() - 2).getOriginalAmount());
//            LOG.info("卖1: {}-{}", asks.get(asks.size() - 1).getLimitPrice(), asks.get(asks.size() - 1).getOriginalAmount());
//            LOG.info("买1: {}-{}", bids.get(0).getLimitPrice(), bids.get(0).getOriginalAmount());
//            LOG.info("买2: {}-{}", bids.get(1).getLimitPrice(), bids.get(1).getOriginalAmount());
//            LOG.info(" ================= depth 记录数 =================ask:{}- bids:{}", orderBook.getAsks().size(), orderBook.getBids().size());
//
//        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));
//        Thread.sleep(5000);
//        depthDisposable.dispose();
//
//        CurrencyPair currencyPair = new CurrencyPair("BTC","USDT");
//        AliasEnum aliasEnum = AliasEnum.THIS_WEEK;
//        Disposable tradeDisposable = exchange.getStreamingMarketDataService().getTrades(currencyPair).subscribe(trades -> {
//            LOG.info(" ================= trade 记录数 =================: {}", trades.size());
//        }, throwable -> LOG.error("ERROR in getting trade: ", throwable));
//        Thread.sleep(10000);
//        tradeDisposable.dispose();
//
//        CurrencyPair currencyPair = new CurrencyPair("BTC","USDT");
//        AliasEnum aliasEnum = AliasEnum.THIS_WEEK;
//        Disposable tradeReqDisposable = exchange.getStreamingMarketDataService().getTradesOnce(currencyPair,aliasEnum).subscribe(trades -> {
//            LOG.info(" ================= tradeReq 记录数 =================: {}", trades.size());
//        }, throwable -> LOG.error("ERROR in req trade: ", throwable));
//        Thread.sleep(2000);
//        tradeReqDisposable.dispose();
    }
}
