package com.troy.streamingexchange.gateio;

import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Completable;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioStreamingExchange implements StreamingExchange {
    private static final String API_URI = "wss://ws.gateio.ws/v3/";

    private final GateioStreamingService streamingService;
    private StreamingMarketDataService streamingMarketDataService;

    public GateioStreamingExchange() {
        this.streamingService = new GateioStreamingService(API_URI);
    }

    @Override
    public void initServices() {
        streamingMarketDataService = new GateioStreamingMarketDataServiceImpl(streamingService);
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
    public void useCompressedMessages(boolean compressedMessages) {

    }

}
