package com.troy.trade.ws.streamingexchange.core;

import io.reactivex.Completable;

public interface StreamingExchange {

    /**
     * service 初始化
     */
    void initServices();

    /**
     * Connects to the WebSocket API of the exchange.
     *
     * @param args Product subscription is used only in certain exchanges where you need to specify subscriptions during the connect phase.
     * @return {@link Completable} that completes upon successful connection.
     */
    Completable connect(ProductSubscription... args);

    /**
     * Disconnect from the WebSocket API.
     *
     * @return {@link Completable} that completes upon successful disconnect.
     */
    Completable disconnect();

    /**
     * Checks whether connection to the exchange is alive.
     *
     * @return true if connection is open, otherwise false.
     */
    boolean isAlive();

    /**
     * Returns service that can be used to access market data.
     */
    StreamingMarketDataService getStreamingMarketDataService();

    /**
     * Set whether or not to enable compression handler.
     *
     * @param compressedMessages Defaults to false
     */
    void useCompressedMessages(boolean compressedMessages);

    /**
     * 做数据初始化处理
     */
    default void applyData(){this.initServices();};

}
