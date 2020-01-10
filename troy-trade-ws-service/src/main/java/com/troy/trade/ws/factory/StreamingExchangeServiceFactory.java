package com.troy.trade.ws.factory;

import com.google.common.collect.Maps;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.trade.ws.service.streaming.IStreamingExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * StreamingExchangeService Factory
 */
@Slf4j
@Component
public class StreamingExchangeServiceFactory implements ApplicationContextAware, InitializingBean {

    private Map<ExchangeCode, IStreamingExchangeService> exchangeMap = Maps.newHashMap();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, IStreamingExchangeService> exchangeMapSpring = this.applicationContext.getBeansOfType(IStreamingExchangeService.class);
        if (CollectionUtils.isEmpty(exchangeMapSpring)) {
            return;
        }
        exchangeMapSpring.forEach((beanName, exchange) -> exchangeMap.put(exchange.getExchCode(), exchange));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<ExchangeCode, IStreamingExchangeService> getExchange() {
        return exchangeMap;
    }

    public IStreamingExchangeService getStreamingExchangeService(ExchangeCode exchCode){
        if(null == exchangeMap){
            log.warn("获取IStreamingExchangeService 失败，当前交易所{}未对接",exchCode);
            return null;
        }
        return this.exchangeMap.get(exchCode);
    }
}
