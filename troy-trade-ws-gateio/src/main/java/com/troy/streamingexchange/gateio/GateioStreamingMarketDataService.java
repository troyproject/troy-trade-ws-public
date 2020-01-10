package com.troy.streamingexchange.gateio;

import com.troy.streamingexchange.gateio.dto.KlineInterval;
import com.troy.streamingexchange.gateio.service.exception.NotConnectedException;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Observable;

import java.util.List;

/**
 * GateioStreamingMarketDataService
 *
 * @author liuxiaocheng
 * @date 2018/7/16
 */
public interface GateioStreamingMarketDataService extends StreamingMarketDataService {

    /**
     * Get a ticker representing the current exchange rate.
     * Emits {@link NotConnectedException} When not connected to the WebSocket API.
     *
     * @param currencyPair Currency pair of the ticker
     * @return {@link Observable} that emits {@link Ticker} when exchange sends the update.
     */
    Observable<List<Ticker>> getKline(CurrencyPair currencyPair, KlineInterval klineInterval, Object... args);

//    /**
//     * Get an order book representing the current offered exchange rates (market depth).
//     * Emits {@link NotConnectedException} When not connected to the WebSocket API.
//     *
//     * @param currencyPair Currency pair of the order book
//     * @return {@link Observable} that emits {@link OpenOrders} when exchange sends the update.
//     */
//    Observable<List<GateioOrderUpdate>> getOpenOrders(CurrencyPair currencyPair, Object... args);

    void ping(Object... args);


    Observable<Object> authenticated(String apiKey, String secretKey);


}
