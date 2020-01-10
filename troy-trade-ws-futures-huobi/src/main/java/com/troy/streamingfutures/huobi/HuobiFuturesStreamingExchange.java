package com.troy.streamingfutures.huobi;

import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Completable;


public class HuobiFuturesStreamingExchange implements StreamingExchange {
    private String apiUri = "wss://www.btcgateway.pro/ws";
    private final HuobiFuturesStreamingService streamingService;
    private HuobiFuturesStreamingMarketDataService streamingMarketDataService;

    public HuobiFuturesStreamingExchange() {
        this.streamingService = new HuobiFuturesStreamingService(apiUri);
    }

    @Override
    public void initServices() {
        streamingMarketDataService = new HuobiFuturesStreamingMarketDataService(streamingService);
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
