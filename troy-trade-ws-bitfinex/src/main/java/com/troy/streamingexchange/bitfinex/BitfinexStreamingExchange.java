package com.troy.streamingexchange.bitfinex;

import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Completable;

public class BitfinexStreamingExchange implements StreamingExchange {
    private static final String API_URI = "wss://dddddd.bitfinex.com/ws/2";

    private final BitfinexStreamingService streamingService;
    private BitfinexStreamingMarketDataService streamingMarketDataService;

    public BitfinexStreamingExchange() {
        this.streamingService = new BitfinexStreamingService(API_URI);
    }

    @Override
    public void initServices() {
        streamingMarketDataService = new BitfinexStreamingMarketDataService(streamingService);
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        return streamingService.connect();
    }

    @Override
    public Completable disconnect() {
        return streamingService.disconnect();
    }

    @Override
    public boolean isAlive() {
        return streamingService.isSocketOpen();
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }

    @Override
    public void applyData() {
        this.initServices();
    }

}
