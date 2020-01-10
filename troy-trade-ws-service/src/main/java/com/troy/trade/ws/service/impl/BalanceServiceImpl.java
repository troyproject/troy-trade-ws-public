package com.troy.trade.ws.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.trade.futures.api.model.dto.in.account.FuturesBalanceReqDto;
import com.troy.trade.futures.api.model.dto.out.account.FuturesAccountInfoResDto;
import com.troy.trade.futures.api.model.dto.out.account.FuturesBalanceResDto;
import com.troy.trade.futures.api.model.dto.out.account.FuturesPositionResDto;
import com.troy.trade.ws.api.model.dto.in.BalanceChangeReqDto;
import com.troy.trade.ws.api.model.dto.in.FuturesAccountInfoReqDto;
import com.troy.trade.ws.api.model.dto.in.FuturesBalanceInfoReqDto;
import com.troy.trade.ws.api.model.dto.in.FuturesPositionInfoReqDto;
import com.troy.trade.ws.api.model.dto.out.BalanceResDto;
import com.troy.trade.ws.feign.FuturesAccountClient;
import com.troy.trade.ws.model.dto.in.BalancePushReqDto;
import com.troy.trade.ws.model.dto.in.BalanceSubscribe;
import com.troy.trade.ws.model.dto.out.FuturesSessionKeyDecodeDto;
import com.troy.trade.ws.model.dto.out.ResponseDto;
import com.troy.trade.ws.model.enums.MethodEnum;
import com.troy.trade.ws.server.NotificationService;
import com.troy.trade.ws.server.SessionUtil;
import com.troy.trade.ws.service.IBalanceService;
import com.troy.trade.ws.thread.FuturesBalancePushSyncExecute;
import com.troy.trade.ws.thread.PushThreadPool;
import com.troy.trade.ws.util.BalancePushUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 余额订阅相关处理
 */
@Component
@Slf4j
public class BalanceServiceImpl implements IBalanceService {


    @Autowired
    private FuturesAccountClient futuresAccountClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BalanceResDto sendSingleFuturesBalance(FuturesAccountInfoReqDto futuresAccountInfoReqDto) {
        /**
         * 4、做账户余额信息推送
         */
        Long accountId = futuresAccountInfoReqDto.getAccountId();
        String symbol = futuresAccountInfoReqDto.getSymbol();
        AliasEnum alias = futuresAccountInfoReqDto.getAlias();
        ExchangeCode exchangeCode = futuresAccountInfoReqDto.getExchCode();

        FuturesBalanceInfoReqDto futuresBalanceInfoReqDto = futuresAccountInfoReqDto.getFuturesBalanceInfoReqDto();
        FuturesBalanceResDto futuresBalanceResDto = new FuturesBalanceResDto();
        BeanUtils.copyProperties(futuresBalanceInfoReqDto, futuresBalanceResDto);
        List<FuturesPositionResDto> positions = new ArrayList<>();
        FuturesPositionResDto futuresPositionResDto = null;

        List<FuturesPositionInfoReqDto> futuresPositionInfoReqDtos = futuresBalanceInfoReqDto.getFuturesPositionInfoReqDtos();
        int size = futuresPositionInfoReqDtos == null?0:futuresPositionInfoReqDtos.size();
        for(int i=0;i<size;i++){
            futuresPositionResDto = new FuturesPositionResDto();
            BeanUtils.copyProperties(futuresPositionInfoReqDtos.get(i), futuresPositionResDto);
            positions.add(futuresPositionResDto);
        }
        futuresBalanceResDto.setPositions(positions);

        BalancePushReqDto<FuturesBalanceResDto> balancePushReqDto = new BalancePushReqDto();
        balancePushReqDto.setAccountId(accountId);
        balancePushReqDto.setExchCode(exchangeCode);
        balancePushReqDto.setSymbol(symbol);
        balancePushReqDto.setAlias(alias);
        balancePushReqDto.setBalance(futuresBalanceResDto);
        BalancePushUtil.sendFuturesBalance(balancePushReqDto);
        return new BalanceResDto(true);
    }

    @Override
    public BalanceResDto sendAllFuturesBalance() {

        log.info("所有订阅用户余额信息变动推送，做余额变动推送触发 ----- 开始");

        /**
         * 验证是否存在用户订阅
         */
        ConcurrentMap<String, Map<String,Set<String>>> accountSessionsMap = SessionUtil.getAccountSessionsMap(true);
        if(CollectionUtils.isEmpty(accountSessionsMap)){//当前不存在订阅余额变动信息的用户
            log.warn("所有订阅用户余额信息变动推送，当前不存在账户订阅，不做余额变动推送，accountSessionsMap={}",accountSessionsMap);
            return new BalanceResDto(false);
        }

        /**
         * 遍历账户及账户下的币对列表，做余额信息查询及推送
         */
        log.info("所有订阅用户余额信息变动推送，做余额变动推送，accountSessionsMap={}",accountSessionsMap);
        FuturesSessionKeyDecodeDto futuresSessionKeyDecodeDto = null;
        Long tempAccountId = null;
        for (Map.Entry<String, Map<String,Set<String>>> entry:accountSessionsMap.entrySet()) {
            Map<String,Set<String>> symbolSessionsMap = entry.getValue();
            if(CollectionUtils.isEmpty(symbolSessionsMap)){
                continue;
            }

            tempAccountId = Long.parseLong(entry.getKey());
            for (Map.Entry<String,Set<String>> symbolKeyEntry:symbolSessionsMap.entrySet()) {
                futuresSessionKeyDecodeDto = SessionUtil.decodeSessionMapKey(symbolKeyEntry.getKey());
                try {
                    log.info("所有订阅用户余额信息变动推送，做余额变动推送，当前futuresSessionKeyDecodeDto={}",objectMapper.writeValueAsString(futuresSessionKeyDecodeDto));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                try {
                    FuturesBalancePushSyncExecute futuresBalancePushSyncExecute
                            = FuturesBalancePushSyncExecute.getInstance(tempAccountId,
                            futuresSessionKeyDecodeDto.getSymbol(), futuresSessionKeyDecodeDto.getAlias());
                    PushThreadPool.executeFuturesBalanceSync(futuresBalancePushSyncExecute);
                }catch (Throwable throwable){
                    log.error("所有订阅用户余额信息变动推送，将任务放入线程池时失败，异常信息：",throwable);
                    continue;
                }
            }
        }
        return new BalanceResDto(true);
    }

    @Override
    public BalanceResDto sendSpotBalance(BalanceChangeReqDto balanceChangeReqDto) {
        return null;
    }

    @Override
    public void subscribe(BalanceSubscribe balanceSubscribe, Long userId, String key, String destination) {
        try {
            ExchangeCode exchCode = balanceSubscribe.getExchangeCode();
            boolean futuresBo = NotificationService.isFutures(exchCode);
            if(futuresBo){//合约
                futuresSubSendBalance(balanceSubscribe,userId,key,destination);
            }else{//非合约
                spotSubSendBalance(balanceSubscribe,userId,key,destination);
            }
        }catch (Throwable throwable){
            log.error("用户当前委托订阅，查询用户初始当前挂单列表异常，异常信息：",throwable);
        }
    }

    /**
     * 做现货当前挂单推送处理
     * @param balanceSubscribe
     */
    private void futuresSubSendBalance(BalanceSubscribe balanceSubscribe, Long userId,String key, String destination){
        String symbol = balanceSubscribe.getSymbol();
        AliasEnum aliasEnum = balanceSubscribe.getAlias();
        String accountIdStr = balanceSubscribe.getAccountId();
        Long accountId = Long.parseLong(accountIdStr);

//        Long accountId,
//        String symbol,
//        AliasEnum alias,
//        Long userId

        FuturesBalanceReqDto futuresBalanceReqDto = FuturesBalanceReqDto.getInstance(accountId,symbol,aliasEnum,userId);
        Req<FuturesBalanceReqDto> futuresBalanceReqDtoReq = ReqFactory.getInstance().createReq(futuresBalanceReqDto);

        log.info("用户余额信息委托订阅，调用当前账户余额接口入参：" + JSONObject.toJSONString(futuresBalanceReqDtoReq));

        Res<FuturesAccountInfoResDto> futuresAccountInfoResDtoRes = futuresAccountClient.balance(futuresBalanceReqDtoReq);

        boolean successBo = true;
        if (null == futuresAccountInfoResDtoRes) {
            successBo = false;
            log.info("用户余额信息委托订阅，调用tradeFutures系统查询当前委托列表失败，tradeFutures系统返回空");
        }else if (!futuresAccountInfoResDtoRes.isSuccess()) {
            successBo = false;
            try {
                log.info("用户余额信息委托订阅，调用tradeFutures系统查询当前委托列表失败，tradeFutures系统返回："
                        + objectMapper.writeValueAsString(futuresAccountInfoResDtoRes));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if(!successBo){
            NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
            ResponseDto responseDto = notificationService.turnNotification("", MethodEnum.BALANCE_SUBSCRIBE.getType());
            notificationService.broadcast(key,destination,responseDto);
            return;
        }

        log.info("用户余额信息委托订阅，调用当前账户余额信息接口返回：" + JSONObject.toJSONString(futuresAccountInfoResDtoRes));

        FuturesAccountInfoResDto futuresAccountInfoResDto = futuresAccountInfoResDtoRes.getData();
        if(null == futuresAccountInfoResDto){
            NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
            ResponseDto responseDto = notificationService.turnNotification("", MethodEnum.BALANCE_SUBSCRIBE.getType());
            notificationService.broadcast(key,destination,responseDto);
            return;
        }

        BalancePushReqDto<FuturesBalanceResDto> balancePushReqDto = new BalancePushReqDto();
        balancePushReqDto.setAccountId(accountId);
        balancePushReqDto.setExchCode(futuresAccountInfoResDto.getExchCode());
        balancePushReqDto.setSymbol(symbol);
        balancePushReqDto.setAlias(aliasEnum);
        balancePushReqDto.setBalance(futuresAccountInfoResDto.getFuturesBalanceResDto());
        BalancePushUtil.sendFuturesBalance(balancePushReqDto);
    }

    /**
     * 做现货当前挂单推送处理
     * @param balanceSubscribe
     */
    private void spotSubSendBalance(BalanceSubscribe balanceSubscribe,Long userId,String key,String destination){
        //TODO 做币币余额变动推送
    }

    /**
     * 币币余额变动推送
     * @param balancePushReqDto
     * @return
     */
    private BalanceResDto sendSpotBalance(BalancePushReqDto balancePushReqDto) {
        return null;
    }
}
