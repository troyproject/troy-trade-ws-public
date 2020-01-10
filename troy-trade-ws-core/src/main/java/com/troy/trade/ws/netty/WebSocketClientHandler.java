package com.troy.trade.ws.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketClientHandler.class);

    public interface WebSocketMessageHandler {
        public void onMessage(String message);
    }

    private final WebSocketClientHandshaker handshaker;
    private final WebSocketMessageHandler handler;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
        this.handshaker = handshaker;
        this.handler = handler;
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
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                LOG.info("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                LOG.error("WebSocket Client failed to connect. {}", e.getMessage());
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            handler.onMessage(textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            ByteBuf content = frame.content();
            if (!frame.isFinalFragment()) {
                byte[] bytes = new byte[content.readableBytes()];
                for (int i = 0; i < content.readableBytes(); i++) {
                    bytes[i] = content.getByte(i);
                }
                LOG.info("======================not FinalFragment======================");
            }
            if(ch.remoteAddress().toString().contains("okex")){
                String message  = uncompress(content);
                handler.onMessage(message);
            }else{
                byte[] bytes = new byte[content.readableBytes()];
                for (int i = 0; i < content.readableBytes(); i++) {
                    bytes[i] = content.getByte(i);
                }
                bytes = uncompress(bytes);
                handler.onMessage(byteBufferToString(ByteBuffer.wrap(bytes)));
            }
        } else if (frame instanceof ContinuationWebSocketFrame) {
            LOG.info("======================ContinuationWebSocketFrame======================");
        } else if (frame instanceof PongWebSocketFrame) {
            LOG.debug("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            LOG.info("WebSocket Client received closing");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.debug("", cause);
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    public byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            LOG.error("gzip uncompress error.", e);
        }

        return out.toByteArray();
    }
    // 解压函数
    public String uncompress(ByteBuf byteBuf){
        byte [] temp = new byte[byteBuf.readableBytes()];
        ByteBufInputStream bis = new ByteBufInputStream(byteBuf);
        StringBuilder appender = new StringBuilder();
        try {
            bis.read(temp);
            bis.close();
            Inflater infl = new Inflater(true);
            infl.setInput(temp,0,temp.length);
            byte [] result = new byte[1024];
            while (!infl.finished()){
                int length = infl.inflate(result);
                appender.append(new String(result,0,length,"UTF-8"));
            }
            infl.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appender.toString();
    }

    //buffer转String
    public static String byteBufferToString(ByteBuffer buffer) {
        CharBuffer charBuffer = null;
        try {
            Charset charset = Charset.forName("ISO-8859-1");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}