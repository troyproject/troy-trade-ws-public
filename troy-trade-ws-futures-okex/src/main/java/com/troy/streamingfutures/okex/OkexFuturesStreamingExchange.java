package com.troy.streamingfutures.okex;

import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Completable;

/**
 * Created by Lukas Zaoralek on 17.11.17.O
 */
public class OkexFuturesStreamingExchange implements StreamingExchange {
    private static final String API_URI = "wss://real.okex.com:8443/ws/v3";

    private final OkexFuturesStreamingService streamingService;

    private OkexFuturesStreamingMarketDataService streamingMarketDataService;

    public OkexFuturesStreamingExchange() {
        streamingService = new OkexFuturesStreamingService(API_URI);
    }

    protected OkexFuturesStreamingExchange(OkexFuturesStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    public void initServices() {
        streamingMarketDataService = new OkexFuturesStreamingMarketDataService(streamingService);
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
