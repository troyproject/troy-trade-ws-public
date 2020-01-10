package com.troy.trade.ws.server;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.trade.ws.factory.StreamingExchangeServiceFactory;
import com.troy.trade.ws.model.dto.in.ConnectDto;
import com.troy.trade.ws.model.dto.in.DisconnectDto;
import com.troy.trade.ws.service.streaming.IStreamingExchangeService;
import com.troy.trade.ws.util.BusinessMethodsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PresenceChannelInterceptor
 * 通道拦截器
 */
@Slf4j
@Component
public class PresenceChannelInterceptor extends ChannelInterceptorAdapter {

    @Autowired
    private StreamingExchangeServiceFactory streamingExchangeServiceFactory;

    /**
     * 发送前处理
     * 判断客户端的连接状态,进行对应处理
     *
     * @param message
     * @param channel
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        // ignore non-STOMP messages like heartbeat messages
        if (sha.isHeartbeat()) {
            return message;
        }

        log.debug("========preSend.sha.command=======:{}", sha.getCommand());

        log.info("客户端请求cmd为："+sha.getCommand());
        switch (sha.getCommand()) {
            case CONNECT:
                boolean connected = doConnection(sha);
                if (!connected) {
                    return null;
                }
                break;
            case DISCONNECT:
                boolean disConnected = doDisConnection(sha);
                if (!disConnected) {
                    return null;
                }
                break;
            default:
                break;
        }

        return message;
    }

    private boolean doConnection(StompHeaderAccessor sha){
        boolean resultBo = false;

        boolean valiResult = BusinessMethodsUtil.presenceChannelVali(sha);
        if(!valiResult){
            return resultBo;
        }

        List<String> exchCodes = sha.getNativeHeader("exchCode");
        int size = exchCodes == null?0:exchCodes.size();
        if(size>0){
            String exchCode = exchCodes.get(0);
            ExchangeCode exchangeCode = ExchangeCode.getExchangeCode(exchCode.toLowerCase());
            IStreamingExchangeService streamingExchangeService = streamingExchangeServiceFactory.getStreamingExchangeService(exchangeCode);
            ConnectDto connectDto = new ConnectDto();
            connectDto.setSessionId(BusinessMethodsUtil.getSessionId(sha));
            connectDto.setSymbol(BusinessMethodsUtil.getPair(sha));
            connectDto.setRobot(false);
            boolean connected = streamingExchangeService.connect(connectDto);
            if (connected) {
                resultBo = true;
            }
        }
        return resultBo;
    }

    private boolean doDisConnection(StompHeaderAccessor sha){
        log.info("用户断开连接，doDisConnection ----- 开始");
        boolean resultBo = false;

        boolean valiResult = BusinessMethodsUtil.presenceChannelVali(sha);
        log.info("用户断开连接，调用BusinessMethodsUtil.presenceChannelVali验证结果 {}",valiResult);
        if(!valiResult){
            return resultBo;
        }

        List<String> exchCodes = sha.getNativeHeader("exchCode");
        log.info("用户断开连接，doDisConnection 入参：exchCodes={}", JSONObject.toJSONString(exchCodes));
        int size = exchCodes == null?0:exchCodes.size();
        if(size>0){
            String exchCode = exchCodes.get(0);
            log.info("用户断开连接，执行断开操作 exchCode {}",exchCode);
            ExchangeCode exchangeCode = ExchangeCode.getExchangeCode(exchCode.toLowerCase());
            IStreamingExchangeService streamingExchangeService = streamingExchangeServiceFactory.getStreamingExchangeService(exchangeCode);
            DisconnectDto disconnectDto = new DisconnectDto();
            disconnectDto.setClientId(BusinessMethodsUtil.getAccountId(sha));
            disconnectDto.setExchCode(BusinessMethodsUtil.getExchCode(sha));
            disconnectDto.setSessionId(BusinessMethodsUtil.getSessionId(sha));

            resultBo = streamingExchangeService.disconnect(disconnectDto);
        }
        log.info("用户断开连接，断开结果 {}，doDisConnection ----- 结束",resultBo);
        return resultBo;
    }
}
