package com.troy.trade.ws.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

/**
 * WebSocketConfig
 * websocket 配置类
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private PresenceChannelInterceptor presenceChannelInterceptor;

    /**
     * 设置代理消息前缀,服务端与客户端心跳机制开启[30000,30000][服务端每30s发送心跳,客户端每30发送心跳]
     * 设置应用前缀,所有@MessageMapping 声明的服务,客户端需要加上app前缀访问
     * 设置用户目的地前缀,主要用于点对点单播模式
     *
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user")
                .setHeartbeatValue(new long[]{30000l, 30000l})
                .setTaskScheduler(webSocketHeartbeatTaskScheduler());
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * 设置endpoint ws
     * 配置握手拦截器
     * 允许跨域访问
     * 开启SockJs
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new StompMessageHandshakeHandler())
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .withSockJS();
    }

    @Bean
    ThreadPoolTaskScheduler webSocketHeartbeatTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration webSocketTransportRegistration) {

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
        channelRegistration.interceptors(presenceChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration channelRegistration) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {

    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> list) {
        return false;
    }

    @Bean
    public WebSocketHandler subProtocolWebSocketHandler() {
        return new CustomSubProtocolWebSocketHandler(clientInboundChannel(), clientOutboundChannel());
    }
}
