package com.troy.trade.ws.feign;

import com.troy.trade.exchange.api.service.MarketExchangeApi;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(qualifier = "marketExchangeClient", name = "${troy.exchange.serviceName}")
public interface MarketExchangeClient extends MarketExchangeApi {
}
