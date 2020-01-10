package com.troy.streamingexchange.huobi;

import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Completable;


public abstract class HuobiStreamingExchange implements StreamingExchange {
    private String apiUri;
    private final HuobiStreamingService streamingService;
    private HuobiStreamingMarketDataService streamingMarketDataService;

    public HuobiStreamingExchange(String apiUri) {
        this.apiUri = apiUri;
        this.streamingService = new HuobiStreamingService(apiUri);
    }

    @Override
    public void initServices() {
        streamingMarketDataService = new HuobiStreamingMarketDataService(streamingService);
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
        streamingService.useCompressedMessages(compressedMessages);
    }

}
