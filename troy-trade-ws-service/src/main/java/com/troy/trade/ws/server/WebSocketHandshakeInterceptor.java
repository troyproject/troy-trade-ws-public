package com.troy.trade.ws.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * WebSocketHandshakeInterceptor
 * 客户端与服务端握手拦截器
 * @author liuxiaocheng
 * @date 2018/6/29
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        LOGGER.info("=========afterHandshake=========");
        super.afterHandshake(request, response, wsHandler, ex);
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map attributes) throws Exception {
        LOGGER.info("=========beforeHandshake=========");
//        if (request instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
//            HttpSession session = servletRequest.getServletRequest().getSession();
//            attributes.put("sessionId", session.getId());
//        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
