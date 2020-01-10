package com.troy.streamingexchange.gateio.service.netty;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.troy.streamingexchange.gateio.dto.GateioWebSocketSubscriptionMessage;
import com.troy.streamingexchange.gateio.dto.GateioWebsocketTypes;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketClientHandler.class);

    public interface WebSocketMessageHandler {
        public void onMessage(String message);
    }

    private final WebSocketClientHandshaker handshaker;

    private final WebSocketMessageHandler handler;
    private ChannelPromise handshakeFuture;
    private final ObjectMapper objectMapper;


    public WebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
        this.handshaker = handshaker;
        this.handler = handler;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOG.info("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            LOG.info("WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            handler.onMessage(textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            LOG.debug("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            LOG.info("WebSocket Client received closing");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = "";
            if (event.state() == IdleState.READER_IDLE) {
                type = "read idle";
            } else if (event.state() == IdleState.WRITER_IDLE) {
                type = "write idle";
            } else if (event.state() == IdleState.ALL_IDLE) {
                type = "all idle";
            }

            int requestId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
            GateioWebSocketSubscriptionMessage subscribeMessage = new GateioWebSocketSubscriptionMessage(requestId, GateioWebsocketTypes.SERVER_PING.getSerializedValue(), Lists.newArrayList());
            String message = objectMapper.writeValueAsString(subscribeMessage);
            WebSocketFrame frame = new TextWebSocketFrame(message);
            ctx.channel().writeAndFlush(frame);
//            LOG.info("{} 超时类型：{}，向服务器发送心跳数据 {}", ctx.channel().remoteAddress(), type, message);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}