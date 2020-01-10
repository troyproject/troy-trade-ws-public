package com.troy.trade.ws.feign;


import com.troy.trade.api.service.AccountApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 账户信息feign 处理
 */
@FeignClient(qualifier = "accountApiFeign", name = "${troy.trade.serviceName}",configuration = FeignConfiguration.class)
public interface AccountApiFeign extends AccountApi {

}