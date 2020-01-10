package com.troy.trade.ws;

import com.troy.trade.ws.model.constant.Constant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * TroyTradeWsApplication
 * @author
 */
@EnableFeignClients(basePackages = {"com.troy.trade.ws.feign"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = Constant.PROJECT_BASE_PACKAGE)
public class TroyTradeWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TroyTradeWsApplication.class, args);
    }



}
