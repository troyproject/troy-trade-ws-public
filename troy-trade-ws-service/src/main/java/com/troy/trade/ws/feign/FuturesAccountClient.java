package com.troy.trade.ws.feign;

import com.troy.trade.futures.api.service.FuturesAccountApi;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(qualifier = "futuresAccountClient", name = "${troy.tradeFutures.serviceName}",configuration = FeignConfiguration.class)
public interface FuturesAccountClient extends FuturesAccountApi {
}
