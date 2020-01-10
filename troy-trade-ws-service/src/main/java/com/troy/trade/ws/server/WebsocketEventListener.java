package com.troy.trade.ws.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * SubscribeEventListener
 * 订阅事件、取消订阅事件监听
 */
@Slf4j
@Component
public class WebsocketEventListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof SessionSubscribeEvent) {
            SessionSubscribeEvent sessionSubscribeEvent = (SessionSubscribeEvent) applicationEvent;
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());
            String key = sha.getSessionId();
            if (SessionUtil.getSessions().containsKey(key)) {
                log.debug("exchange已经完成初始化,key:{}", key);
            }
            log.debug("=======================订阅:{}==================", key);

        }
        if (applicationEvent instanceof SessionUnsubscribeEvent) {
            SessionUnsubscribeEvent sessionUnsubscribeEvent = (SessionUnsubscribeEvent) applicationEvent;
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sessionUnsubscribeEvent.getMessage());
            String key = sha.getSessionId();
            log.debug("=======================取消订阅:{}==================", key);
        }

        if (applicationEvent instanceof SessionDisconnectEvent) {
            SessionDisconnectEvent sessionDisconnectEvent = (SessionDisconnectEvent) applicationEvent;
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
            String key = sha.getSessionId();
            log.debug("=======================客户端断开连接:{}==================", key);
        }

    }

}
