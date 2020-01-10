package com.troy.trade.ws.server;

import com.google.common.collect.Maps;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.utils.EnumUtils;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.model.dto.out.FuturesSessionKeyDecodeDto;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * SessionUtil
 */
@Slf4j
public class SessionUtil {

    /**
     * 合约余额订阅信息<账户ID,<symbol,sessionSet>>
     */
    private static final ConcurrentMap<String, Map<String,Set<String>>> spotAccountSessionsMap = Maps.newConcurrentMap();


    /**
     * 合约余额订阅信息<账户ID,<symbol_alias,sessionSet>>
     */
    private static final ConcurrentMap<String, Map<String,Set<String>>> futuresAccountSessionsMap = Maps.newConcurrentMap();

    /**
     * 客户端订阅Streaming
     */
    private static final ConcurrentMap<String, StreamingExchange> sessions = Maps.newConcurrentMap();

    public static ConcurrentMap<String, StreamingExchange> getSessions() {
        return sessions;
    }

    public static StreamingExchange getStreamingExchange(String key) {
        return sessions.get(key);
    }

    /**
     * 生成Key（交易所code+"_"+sessionId)
     *
     * @param sha
     * @return
     */
    public static String genKey(SimpMessageHeaderAccessor sha, String exchCode, String pairPath) {
        return exchCode + "_" + sha.getSessionId() + "_" + pairPath;
    }

    /**
     * 生成Key
     *
     * @param sessionId
     * @param exchCode
     * @param symbol
     * @return
     */
    public static String genKey(String sessionId, String exchCode, String symbol) {
        String pairPath = symbol.replace("/", "_").toLowerCase();
        return exchCode + "_" + sessionId + "_" + pairPath;
    }


    /**
     * 账户session信息获取
     * @param isFutures
     * @return
     */
    public static ConcurrentMap<String, Map<String,Set<String>>> getAccountSessionsMap(boolean isFutures ){
        if(isFutures){
            return futuresAccountSessionsMap;
        }else{
            return spotAccountSessionsMap;
        }
    }

    /**
     * 释放交易所资源
     *
     * @param sessionId 唯一key
     * @param accountId 客户端ID
     */
    public static void removeSession(String sessionId, String accountId) {
        log.info("断开连接1：Disconnect  sessions disconnect sessionId:{},accountId:{}", sessionId, accountId);
        if (sessions.get(sessionId) != null) {
            sessions.get(sessionId).disconnect().subscribe(() -> {
                log.info("断开连接2：Disconnected,sessionId:[{}],sessions:[{}]", sessionId,sessions);
                sessions.remove(sessionId);
                log.info("断开连接3：Disconnect client remove sessionId:{},accountId:{}", sessionId, accountId);
                removeSpotClient(accountId, sessionId);
                removeFuturesClient(accountId, sessionId);
                log.info("断开连接4：做session移除后，accountId={},sessionId={},spotAccountSessionsMap={},futuresSessionsKeyMap={},sessions={}",
                        accountId,sessionId, spotAccountSessionsMap,futuresAccountSessionsMap,SessionUtil.getSessions());
            });
        }
    }

    /**
     * 组装accountIdSessionMapKey中key信息
     * @param symbol
     * @param aliasEnum
     * @return
     */
    public static String getAccountIdSessionMapKey(String symbol,AliasEnum aliasEnum){
        if(null == aliasEnum){
            return symbol;
        }

        StringBuffer symbolKey = new StringBuffer(symbol);//symbol@alias
        symbolKey.append(Constant.ACCOUNT_SESSION_KEY_SEPARATOR);
        symbolKey.append(aliasEnum.code());
        return symbolKey.toString();
    }

    /**
     * 将futuresAccountSessionsMap中的key返解析成symbol和aliasEnum
     * @return
     */
    public static FuturesSessionKeyDecodeDto decodeSessionMapKey(String symbolKey){
        if(StringUtils.isBlank(symbolKey)){
            return null;
        }

        String[] arr = symbolKey.split(Constant.ACCOUNT_SESSION_KEY_SEPARATOR);//0-symbol、1-alias
        return FuturesSessionKeyDecodeDto.getInstance(arr[0], EnumUtils.getEnumByCode(arr[1],AliasEnum.class));
    }


    /**
     * 账户信息新增
     * @param accountId
     * @param symbol
     * @param aliasEnum
     * @param sessionId
     * @param isFutures
     * @return
     */
    public static boolean addClient(String accountId, String symbol,
                                    AliasEnum aliasEnum, String sessionId,
                                    boolean isFutures) {
        boolean resultBo;
        String symbolKey = SessionUtil.getAccountIdSessionMapKey(symbol,aliasEnum);
        if(isFutures){
            resultBo = addClient(accountId,symbolKey.toString(), sessionId,futuresAccountSessionsMap);
            log.info("用户合约账户信息订阅，账户订阅信息为：{}", futuresAccountSessionsMap);
        }else {
            log.info("用户币币当前委托订阅，账户订阅信息为：{}", spotAccountSessionsMap);
            resultBo = addClient(accountId,symbolKey.toString(), sessionId,spotAccountSessionsMap);
        }
        return resultBo;
    }

    /**
     * 保存账户订阅信息
     * @param accountId
     * @param symbolKey
     * @param sessionId
     * @return
     */
    private static boolean addClient(String accountId,String symbolKey, String sessionId,
                                     ConcurrentMap<String, Map<String,Set<String>>> accountSessionsMap){
        if (accountSessionsMap.containsKey(accountId)) {
            Map<String,Set<String>> tempMap = accountSessionsMap.get(accountId);
            if(tempMap.containsKey(symbolKey)){
                tempMap.get(symbolKey).add(sessionId);
            }else{
                Set<String> concurrentSet = new HashSet<>();
                concurrentSet.add(sessionId);
                tempMap.put(symbolKey,concurrentSet);
            }
        } else {
            Set<String> concurrentSet = new HashSet<>();
            concurrentSet.add(sessionId);
            Map<String,Set<String>> tempMap = Maps.newConcurrentMap();
            tempMap.put(symbolKey,concurrentSet);
            accountSessionsMap.put(accountId, tempMap);
        }
        return true;
    }

    public static void removeClient(String accountId, String sessionId,boolean isFutures) {
        if(isFutures){
            removeFuturesClient(accountId, sessionId);
        }else{
            removeSpotClient(accountId, sessionId);
        }
    }

    private static boolean removeSpotClient(String accountId, String sessionId) {
        if (StringUtils.isBlank(sessionId)) {//验证当前sessionId是否为空，为空则不处理
            return false;
        }

        log.info("用户当前委托订阅，做用户session移除前，accountId={},sessionId={},spotAccountSessionsMap={}",accountId,sessionId, spotAccountSessionsMap);
        return removeSessionFromMap(accountId, sessionId,spotAccountSessionsMap);
    }

    private static boolean removeFuturesClient(String accountId, String sessionId) {
        if (StringUtils.isBlank(sessionId)) {//验证当前sessionId是否为空，为空则不处理
            return false;
        }

        log.info("用户当前委托订阅，做用户session移除前，accountId={},sessionId={},futuresAccountSessionsMap={}",accountId,sessionId, futuresAccountSessionsMap);
        return removeSessionFromMap(accountId, sessionId,futuresAccountSessionsMap);
    }

    /**
     * 从map中移除session
     * @param accountId
     * @param sessionId
     * @param accountSessionsMap
     * @return
     */
    private static boolean removeSessionFromMap(String accountId, String sessionId,
                                                ConcurrentMap<String, Map<String,Set<String>>> accountSessionsMap){
        boolean removeBo = false;
        if (StringUtils.isNotBlank(accountId)) {
            //客户端正常主动关闭
            if (accountSessionsMap.containsKey(accountId)) {
                if (!CollectionUtils.isEmpty(accountSessionsMap.get(accountId))) {
                    Map<String,Set<String>> sessionsMap = accountSessionsMap.get(accountId);

                    removeBo = removeSessions(sessionId,sessionsMap);

                    if(sessionsMap.isEmpty()){
                        accountSessionsMap.remove(accountId);
                    }
                }
            }
        } else {
            //客户端非正常关闭
            Set<String> keySet = accountSessionsMap.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            int keySize = keyList == null?0:keyList.size();
            Map<String,Set<String>> sessionsMap = null;
            String tempAccountId = null;
            for(int i=0;i<keySize;i++){
                tempAccountId = keyList.get(i);
                sessionsMap = accountSessionsMap.get(tempAccountId);
                if(CollectionUtils.isEmpty(sessionsMap)){
                    sessionsMap.remove(tempAccountId);
                    continue;
                }

                removeBo = removeSessions(sessionId,sessionsMap);

                if(sessionsMap.isEmpty()){
                    accountSessionsMap.remove(tempAccountId);
                }

                if(removeBo){
                    break;
                }
            }
        }

        return removeBo;
    }

    /**
     * 从map中移除session
     * @param sessionId
     * @param sessionsMap
     * @return
     */
    private static boolean removeSessions(String sessionId,Map<String,Set<String>> sessionsMap){
        Set<String> sessionsSet = null;
        boolean removeBo = false;
        for (Map.Entry<String, Set<String>> entry : sessionsMap.entrySet()) {
            sessionsSet = entry.getValue();
            if(sessionsSet.contains(sessionId)){
                sessionsSet.remove(sessionId);
                removeBo = true;
            }
            if(sessionsSet.isEmpty()){
                sessionsMap.remove(entry.getKey());
            }

            if(removeBo){
                break;
            }
        }
        return removeBo;
    }




}
