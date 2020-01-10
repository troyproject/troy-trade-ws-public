package com.troy.trade.ws.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.utils.EnumUtils;
import com.troy.futures.exchange.api.model.dto.in.account.ContractInfoReqDto;
import com.troy.futures.exchange.api.model.dto.out.account.ContractInfoResDto;
import com.troy.redis.RedisUtil;
import com.troy.trade.ws.constants.WsScheduledConstant;
import com.troy.trade.ws.feign.MarketFuturesExchangeClient;
import com.troy.trade.ws.util.BusinessMethodsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 公共合约信息
 */
@Slf4j
@Component
public class InstrumentsSyncUtil {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MarketFuturesExchangeClient marketFuturesExchangeClient;

    /**
     * 获取交易所的公共合约信息
     */
    public void initInstruments() {
        log.info("[获取合约信息]- 同步合约信息---开始");
        List<ExchangeCode> exchangeCodeList = getFuturesExchangeCodes();
        if (CollectionUtils.isEmpty(exchangeCodeList)) {
            log.info("[获取合约信息]-根据ExchCode获取ExchangeCode枚举为空");
            return;
        }

        log.info("[获取合约信息]- 同步合约信息-本次要同步的交易所有：{}",exchangeCodeList);
        exchangeCodeList.forEach(item -> {
            try {
                ContractInfoReqDto contractInfoReqDto = new ContractInfoReqDto();
                contractInfoReqDto.setExchCode(item);
                Res<ResList<ContractInfoResDto>> res = marketFuturesExchangeClient.contractInfo(ReqFactory.getInstance().createReq(contractInfoReqDto));
                log.info("[获取合约信息]- 调用交易所查询合约信息,exchCode={},返回：{}",item,res);
                if (null == res) {
                    log.info("[获取合约信息]-null");
                    return;
                }
                if (!res.isSuccess()) {
                    log.info("[获取合约信息]-[{}]-failed:{}", item.code(), res.getHead());
                    return;
                }

                ContractInfoResDto contractInfoResDto = null;
                Map<String,String> symbolAliasMap = new HashMap<>();
                Map<String,String> instrumentIdMap = new HashMap<>();
                String symbol = null;
                String alias = null;
                String temp = null;
                String contractInfoMapKey = null;
                List<ContractInfoResDto> list = res.getData().getList();
                int size = list == null?0:list.size();
                for(int i=0;i<size;i++){
                    contractInfoResDto = list.get(i);
                    symbol = contractInfoResDto.getNewSymbol();
                    alias = contractInfoResDto.getAlias();

                    contractInfoMapKey = BusinessMethodsUtil.getContractKey(symbol,alias);

                    temp = JSONObject.toJSONString(contractInfoResDto);
                    symbolAliasMap.put(contractInfoMapKey, temp);
                    instrumentIdMap.put(contractInfoResDto.getInstrumentId(),temp);
                }

                //xxxxxange:ws:contractInfo:{exchCode}:symbol_alias
                String symbolAliasRedisKey = WsScheduledConstant.SYNC_CONTRACT_INFO_SYMBOL_ALIAS_MAP_REDIS_KEY.replace("{exchCode}",item.code());
                log.info("[获取合约信息]- 调用交易所查询合约信息,symbolAliasRedisKey={},symbolAliasMap={}",symbolAliasRedisKey,symbolAliasMap);
                redisUtil.remove(symbolAliasRedisKey);
                redisUtil.putAll(symbolAliasRedisKey, symbolAliasMap);

                //xxxxxange:ws:contractInfo:{exchCode}:instrument_id
                String instrumentIdRedisKey = WsScheduledConstant.SYNC_CONTRACT_INFO_INSTRUMENT_ID_MAP_REDIS_KEY.replace("{exchCode}",item.code());
                log.info("[获取合约信息]- 调用交易所查询合约信息,instrumentIdRedisKey={},instrumentIdMap={}",instrumentIdRedisKey,instrumentIdMap);
                redisUtil.remove(instrumentIdRedisKey);
                redisUtil.putAll(instrumentIdRedisKey, instrumentIdMap);
                
                redisUtil.expire(symbolAliasRedisKey, WsScheduledConstant.SYNC_CONTRACT_INFO_MAP_TIME_OUT, TimeUnit.SECONDS);
                redisUtil.expire(instrumentIdRedisKey, WsScheduledConstant.SYNC_CONTRACT_INFO_MAP_TIME_OUT, TimeUnit.SECONDS);

            } catch (Exception e) {
                log.error("[获取合约信息]-[exchangeCode:{}]-error:", item, e);
            }
        });
        log.info("[获取合约信息]- 同步合约信息---成功----结束");
    }

    /**
     * 获取交易所信息
     *
     * @return
     */
    private List<ExchangeCode> getFuturesExchangeCodes() {
        List<ExchangeCode> exchangeCodeList = new ArrayList<>();
        exchangeCodeList.add(ExchangeCode.OKEX_FUTURES_DELIVERY);
        exchangeCodeList.add(ExchangeCode.OKEX_FUTURES_SWAP);
        exchangeCodeList.add(ExchangeCode.HUOBI_FUTURES_DELIVERY);
        return exchangeCodeList;
    }

    /**
     * 获取币对类型code
     * @param exchCode
     * @param instrumentId
     * @return
     */
    public AliasEnum getAliasCode(ExchangeCode exchCode,String instrumentId){
        String instrumentIdRedisKey = WsScheduledConstant.SYNC_CONTRACT_INFO_INSTRUMENT_ID_MAP_REDIS_KEY.replace("{exchCode}",exchCode.code());
        Object contractInfo = redisUtil.entriesMapKey(instrumentIdRedisKey,instrumentId);
        if(null == contractInfo){
            return AliasEnum.THIS_WEEK;
        }else{
            String contractInfoStr = (String)contractInfo;
            ContractInfoResDto contractInfoResDto = JSONObject.parseObject(contractInfoStr, ContractInfoResDto.class);
            return EnumUtils.getEnumByCode(contractInfoResDto.getAlias(),AliasEnum.class);
        }
    }

    /**
     * 获取合约ID
     * @param exchCode
     * @param symbol
     * @param aliasEnum
     * @return
     */
    public String getInstrumentId(ExchangeCode exchCode,String symbol,AliasEnum aliasEnum){
        String instrumentIdRedisKey = WsScheduledConstant.SYNC_CONTRACT_INFO_SYMBOL_ALIAS_MAP_REDIS_KEY.replace("{exchCode}",exchCode.code());
        String contractInfoMapKey = BusinessMethodsUtil.getContractKey(symbol,aliasEnum.code());
        Object contractInfo = redisUtil.entriesMapKey(instrumentIdRedisKey,contractInfoMapKey);
        if(null == contractInfo){
            return null;
        }
        String contractInfoStr = (String)contractInfo;
        ContractInfoResDto contractInfoResDto = JSONObject.parseObject(contractInfoStr, ContractInfoResDto.class);
        return contractInfoResDto.getInstrumentId();
    }
}
