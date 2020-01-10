package com.troy.trade.ws.feign;

import com.troy.trade.futures.api.service.FuturesOrderApi;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(qualifier = "orderApiFuturesFeign", name = "${troy.tradeFutures.serviceName}",configuration = FeignConfiguration.class)
public interface OrderApiFuturesFeign extends FuturesOrderApi {

}
