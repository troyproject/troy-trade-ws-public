package com.troy.trade.ws.configurator;

import com.troy.trade.ws.configurator.properties.UserProperties;
import com.troy.user.client.auth.TokenConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * token 转换配置
 *
 * @author
 * @date 2018-10-25 下午3:59:05
 * @copyright
 */
@Slf4j
@Configuration
public class TokenConverterConfigurator {

    @Autowired
    private UserProperties userProperties;

    /**
     *
     * @return
     */
    @Bean
    TokenConverter TokenConverter() {
        log.info("publicKey = " ,this.userProperties.getPublicKey());
        TokenConverter tokenConverter = TokenConverter.builder().withPublicKey(this.userProperties.getPublicKey()).build();
        return tokenConverter;
    }
}
