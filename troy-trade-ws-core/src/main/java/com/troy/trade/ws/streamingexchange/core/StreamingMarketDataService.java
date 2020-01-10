package com.troy.trade.ws.streamingexchange.core;

import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import io.reactivex.Observable;

import java.util.List;


public interface StreamingMarketDataService {
    /**
     * Get an order book representing the current offered exchange rates (market depth).
     * Emits {@link com.troy.trade.ws.exceptions.NotConnectedException} When not connected to the WebSocket API.
     *
     * @param currencyPair Currency pair of the order book
     * @return {@link Observable} that emits {@link OrderBook} when exchange sends the update.
     */
    Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args);

    /**
     * Get a ticker representing the current exchange rate.
     * Emits {@link com.troy.trade.ws.exceptions.NotConnectedException} When not connected to the WebSocket API.
     *
     * @param currencyPair Currency pair of the ticker
     * @return {@link Observable} that emits {@link Ticker} when exchange sends the update.
     */
    Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args);

    /**
     * 订阅最新成交
     * Get the trades performed by the exchange.
     * Emits {@link com.troy.trade.ws.exceptions.NotConnectedException} When not connected to the WebSocket API.
     *
     * @param currencyPair Currency pair of the trades
     * @return {@link Observable} that emits {@link Trade} when exchange sends the update.
     */
    Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args);


    /**
     * 查询最新成交
     * Get the trades performed by the exchange.
     * Emits {@link com.troy.trade.ws.exceptions.NotConnectedException} When not connected to the WebSocket API.
     *
     * @param currencyPair Currency pair of the trades
     * @return {@link Observable} that emits {@link Trade} when exchange sends the update.
     */
    Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args);





}
