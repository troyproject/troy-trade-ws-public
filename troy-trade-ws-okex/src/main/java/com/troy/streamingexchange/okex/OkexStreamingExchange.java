package com.troy.streamingexchange.okex;

import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Completable;

public class OkexStreamingExchange implements StreamingExchange {
    private static final String API_URI = "wss://real.okex.com:10442/ws/v3";

    private final OkexStreamingService streamingService;
    private OkexStreamingMarketDataService streamingMarketDataService;

    public OkexStreamingExchange() {
        streamingService = new OkexStreamingService(API_URI);
    }

    protected OkexStreamingExchange(OkexStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    public void initServices() {
        streamingMarketDataService = new OkexStreamingMarketDataService(streamingService);
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
}
