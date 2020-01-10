package com.troy.trade.ws.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Slf4j
public class CustomSubProtocolWebSocketHandler extends SubProtocolWebSocketHandler {

    /**
     * Create a new {@code SubProtocolWebSocketHandler} for the given inbound and outbound channels.
     *
     * @param clientInboundChannel  the inbound {@code MessageChannel}
     * @param clientOutboundChannel the outbound {@code MessageChannel}
     */
    public CustomSubProtocolWebSocketHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
        super(clientInboundChannel, clientOutboundChannel);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("用户连接，WebSocketHandler中新连接建立，sessionId={}，New websocket connection was established",session.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("用户断开连接，WebSocketHandler中wesocket连接断开处理 websocket connection:sessionId={} was closed",session.getId());
        SessionUtil.removeSession(session.getId(),null);
    }
}
