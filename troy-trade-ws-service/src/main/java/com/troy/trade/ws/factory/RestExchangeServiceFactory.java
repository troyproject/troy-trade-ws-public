package com.troy.trade.ws.factory;

import com.google.common.collect.Maps;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.trade.ws.service.rest.IRestExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * RestExchangeServiceFactory Factory
 */
@Slf4j
@Component
public class RestExchangeServiceFactory implements ApplicationContextAware, InitializingBean {

    private Map<ExchangeCode, IRestExchangeService> exchangeMap = Maps.newHashMap();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, IRestExchangeService> exchangeMapSpring = this.applicationContext.getBeansOfType(IRestExchangeService.class);
        if (CollectionUtils.isEmpty(exchangeMapSpring)) {
            return;
        }
        exchangeMapSpring.forEach((beanName, exchange) -> exchangeMap.put(exchange.getExchCode(), exchange));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<ExchangeCode, IRestExchangeService> getExchange() {
        return exchangeMap;
    }

    public IRestExchangeService getRestExchangeService(ExchangeCode exchCode){
        if(null == exchangeMap){
            log.warn("获取IRestExchangeService 失败，当前交易所{}未对接",exchCode);
            return null;
        }
        return this.exchangeMap.get(exchCode);
    }
}
