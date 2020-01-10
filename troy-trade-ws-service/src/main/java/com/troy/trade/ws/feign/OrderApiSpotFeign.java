package com.troy.trade.ws.feign;

import com.troy.trade.api.service.OrderApi;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(qualifier = "orderApiSpotFeign", name = "${troy.trade.serviceName}",configuration = FeignConfiguration.class)
public interface OrderApiSpotFeign extends OrderApi {
}
