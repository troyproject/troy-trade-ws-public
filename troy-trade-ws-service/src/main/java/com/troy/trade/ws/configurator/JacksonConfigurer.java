package com.troy.trade.ws.configurator;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Han
 */
@Configuration
public class JacksonConfigurer {

    @Bean("jackson2ObjectMapperBuilderCustomizer")
    Jackson2ObjectMapperBuilderCustomizer configureJackson() {
        Jackson2ObjectMapperBuilderCustomizer customizer = jacksonObjectMapperBuilder ->
                //Long类型的字段在序列化后转换为String类型，避免在传输过程中丢失精度
                jacksonObjectMapperBuilder
                        .serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
        return customizer;
    }
}
