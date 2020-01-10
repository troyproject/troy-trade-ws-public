package com.troy.trade.ws.server;

import com.troy.redis.RedisUtil;
import com.troy.trade.ws.model.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WebScoketLaunchService
 *
 * @author liuxiaocheng
 * @date 2018/6/28
 */
@Service
@Slf4j
public class WebScoketLaunchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private RedisUtil redisUtil;

    private final String ROBOT_SESSONID_PREFIX = "robotSessionId_";
    private final String ROBOT_CLIENTID_PREFIX = "robotClientId_";

    /**
     * 启动gateio exchange
     */
    public void launchRobotWebsocket() {
        logger.debug("-=-=-=-=-=-=-=-=-=- start robot init -=-=-=-=-=-=-=-=-=-");
//        redisUtil.removePattern(Constant.ROBOT_INFO_REDIS_KEY+"*");
//        List<Robot> robots = robotService.queryForList();
//        Map<String, List<Robot>> groupByExchAcctSymbolId = robots.stream().filter(s -> s.getStatus().intValue() != Constant.RobotStatus.DELETE_STATUS.getType()).
//                collect(groupingBy(Robot::getExchAcctSymbolId));
//
//        groupByExchAcctSymbolId.keySet().stream().forEach(exchAcctSymbolId -> {
//            ExchangeAccountSymbol exchangeAccountSymbol = exchangeAccountSymbolService.queryExchAcctSymbolById(exchAcctSymbolId);
//            if (exchangeAccountSymbol != null) {
//                StreamingExchange streamingExchange = getStreamingExchange(exchangeAccountSymbol);
//                if (streamingExchange != null) {
//                    subscribeTicker(exchangeAccountSymbol, streamingExchange);
//                    subscribeDepth(exchangeAccountSymbol, streamingExchange);
//                } else {
//                    logger.error("机器人启动初始化,交易账户异常,直接跳过");
//                }
//            } else {
//                logger.info("机器人启动初始化,交易账户不存在,直接跳过");
//            }
//        });
        logger.debug("-=-=-=-=-=-=-=-=-=- end robot init -=-=-=-=-=-=-=-=-=-");
    }

//    /**
//     * 根据交易对ID订阅深度及ticker服务并放入缓存中供机器人使用
//     *
//     * @param exchAcctSymbolId
//     */
//    public void launchRobotWebsocketByExchAcctSymbolId(String exchAcctSymbolId) {
//        if (redisUtil.exists(Constant.TICKER_KEY + exchAcctSymbolId)) {
//            // 存在相同的则不再订阅
//            logger.warn("存在相同的交易对{}不再订阅", Constant.TICKER_KEY + exchAcctSymbolId);
//            return;
//        }
//
//        ExchangeAccountSymbol exchangeAccountSymbol = exchangeAccountSymbolService.queryExchAcctSymbolById(exchAcctSymbolId);
//        if (exchangeAccountSymbol != null) {
//
//            StreamingExchange streamingExchange = getStreamingExchange(exchangeAccountSymbol);
//            if (streamingExchange != null) {
//                // 存在相同的则不再订阅
//                if (!blockvcRedisUtil.exists(genOrderBookKey(exchangeAccountSymbol))) {
//                    logger.warn("机器人重新订阅depth服务：交易所{} 交易对{}" , exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol());
//                    subscribeDepth(exchangeAccountSymbol, streamingExchange);
//                }
//                logger.warn("机器人重新订阅Ticker服务：交易所{} 交易对{}" , exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol());
//                subscribeTicker(exchangeAccountSymbol, streamingExchange);
//            } else {
//                logger.info("交易对" + exchAcctSymbolId + "深度或者ticker订阅失败，直接跳过");
//            }
//        } else {
//            logger.info("交易对" + exchAcctSymbolId + "信息不存在，直接跳过");
//        }
//    }
//
//    public void reLaunchRobotWebsocket(ExchangeAccountSymbol exchangeAccountSymbol) {
//
//        blockvcRedisUtil.removePattern(genTickerKey(exchangeAccountSymbol));
//        blockvcRedisUtil.removePattern(genOrderBookKey(exchangeAccountSymbol));
//        blockvcRedisUtil.removePattern(Constant.TICKER_KEY + exchangeAccountSymbol.getExchAcctSymbolId());
//
//        if (exchangeAccountSymbol != null) {
//            StreamingExchange streamingExchange = getStreamingExchange(exchangeAccountSymbol);
//            if (streamingExchange != null) {
//                subscribeTicker(exchangeAccountSymbol, streamingExchange);
//                subscribeDepth(exchangeAccountSymbol, streamingExchange);
//            } else {
//                logger.error("机器人启动初始化,交易账户异常,直接跳过");
//            }
//        } else {
//            logger.info("机器人启动初始化,交易账户不存在,直接跳过");
//        }
//
//    }


//    /**
//     * 获取StreamingExchange
//     *
//     * @param exchangeAccountSymbol
//     * @return
//     */
//    private StreamingExchange getStreamingExchange(ExchangeAccountSymbol exchangeAccountSymbol) {
//        Map<String, String> sessionStringMap = createSessionId(exchangeAccountSymbol);
//        String sessionId = sessionStringMap.get("sessionId");
//        String clientId = sessionStringMap.get("clientId");
//        if (!Strings.isNullOrEmpty(exchangeAccountSymbol.getSymbol()) && !Strings.isNullOrEmpty(exchangeAccountSymbol.getExchCode())) {
//            if (ExchangeType.HUOBI.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                return getStreamingExchange(sessionId, clientId, exchangeAccountSymbol.getExchCode(), "hadax".equals(exchangeAccountSymbol.getSymbolType().toLowerCase()));
//            }else if (ExchangeType.HUOBIKO.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                return getStreamingExchange(sessionId, clientId, exchangeAccountSymbol.getExchCode(), "hadax".equals(exchangeAccountSymbol.getSymbolType().toLowerCase()));
//            } else {
//                return getStreamingExchange(sessionId, clientId, exchangeAccountSymbol.getExchCode(), false);
//            }
//        }
//        return null;
//    }

//    /**
//     * 创建机器人sessionId
//     * @param exchangeAccountSymbol
//     * @return
//     */
//    private Map<String, String> createSessionId(ExchangeAccountSymbol exchangeAccountSymbol){
//        Map<String, String> returnMap = Maps.newHashMap();
//        boolean isHadax;
//        String sessionId = ROBOT_SESSONID_PREFIX + exchangeAccountSymbol.getExchCode();
//        String clientId = ROBOT_CLIENTID_PREFIX + exchangeAccountSymbol.getExchCode();
//        if (!Strings.isNullOrEmpty(exchangeAccountSymbol.getSymbol()) && !Strings.isNullOrEmpty(exchangeAccountSymbol.getExchCode())) {
//            if (ExchangeType.HUOBI.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                /**
//                 * huobi有pro和hadax两个交易所
//                 */
//                sessionId = sessionId + ".pro";
//                if (!Strings.isNullOrEmpty(exchangeAccountSymbol.getSymbolType())) {
//                    isHadax = "hadax".equals(exchangeAccountSymbol.getSymbolType().toLowerCase());
//                    if (isHadax) {
//                        sessionId = ROBOT_SESSONID_PREFIX + exchangeAccountSymbol.getExchCode() + ".hadax";
//                        clientId = ROBOT_CLIENTID_PREFIX + exchangeAccountSymbol.getExchCode() + ".hadax";
//                    }
//                }
//            }else if (ExchangeType.HUOBIKO.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                /**
//                 * huobiKO有pro和hadax两个交易所
//                 */
//                sessionId = sessionId + ".pro";
//                if (!Strings.isNullOrEmpty(exchangeAccountSymbol.getSymbolType())) {
//                    isHadax = "hadax".equals(exchangeAccountSymbol.getSymbolType().toLowerCase());
//                    if (isHadax) {
//                        sessionId = ROBOT_SESSONID_PREFIX + exchangeAccountSymbol.getExchCode() + ".hadax";
//                        clientId = ROBOT_CLIENTID_PREFIX + exchangeAccountSymbol.getExchCode() + ".hadax";
//                    }
//                }
//            }else if (ExchangeType.HUOBIEOS.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                /**
//                 * huobiEos
//                 */
//                sessionId = sessionId + ".pro";
//            }else if (ExchangeType.GATEIO.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                /**
//                 * gateio 暂时通过@+交易对,一个交易对一个stream实现
//                 * 需要改造买卖挂单订阅,支持一次订阅多个交易对
//                 */
//                sessionId = sessionId + "@" + exchangeAccountSymbol.getTradeSymbol();
//            } else if (ExchangeType.BINANCE.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//                sessionId = sessionId + "@symbol_" + exchangeAccountSymbol.getSymbol();
//
//            }
//        }
//        returnMap.put("sessionId", sessionId);
//        returnMap.put("clientId", clientId);
//        return returnMap;
//    }
//
//    private StreamingExchange getStreamingExchange(String sessionId, String clientId, String exchangeCode, boolean isHadax) {
//        StreamingExchange streamingExchange = null;
//        if (SessionUtil.getSessions().containsKey(sessionId)) {
//            return SessionUtil.getSessions().get(sessionId);
//        }
//        switch (ExchangeType.toExchangeType(exchangeCode)) {
//            case GATEIO:
//                streamingExchange = ExchangeLaunchSington.getGateioStreamingExchange();
//                StreamingExchange finalStreamingExchange = streamingExchange;
//                Disposable pingDisposable = Observable.interval(5, TimeUnit.SECONDS).subscribe(tick -> {
//                    ((GateioStreamingMarketDataService) finalStreamingExchange.getStreamingMarketDataService()).ping();
//                    logger.debug("==============ping=============:sessionId:{},tick:{}", sessionId, tick);
//                });
//                SessionUtil.subscribe(sessionId, pingDisposable);
//                break;
//            case HUOBIKO:
//                streamingExchange = ExchangeLaunchSington.getHuobikoStreamingExchange();
//                break;
//            case HUOBI:
//                streamingExchange = ExchangeLaunchSington.getHuobiStreamingExchange(isHadax);
//                break;
//            case HUOBIEOS:
//                streamingExchange = ExchangeLaunchSington.getHuobiEosStreamingExchange();
//                break;
//            case BIBOX:
//                streamingExchange = ExchangeLaunchSington.getBiboxStreamingExchange();
//                break;
//            case BITFINEX:
//                streamingExchange = ExchangeLaunchSington.getBitfinexStreamingExchange();
//                break;
////            case KUCOIN:
////                streamingExchange = ExchangeLaunchSington.getKucoinStreamingExchange();
////                break;
//            case BINANCE:
//                String[] strArr = sessionId.split("@symbol_");
//                streamingExchange = ExchangeLaunchSington.getBinanceStreamingExchange(strArr[1],true);
//                break;
//            case OKEX:
//                streamingExchange = ExchangeLaunchSington.getOKEXStreamingExchange();
//                break;
//            default:
//                break;
//        }
//        if (streamingExchange != null) {
//            SessionUtil.getSessions().put(sessionId, streamingExchange);
//            SessionUtil.addClient(clientId, sessionId);
//            logger.debug("机器人启动初始化,sessionId:{}", sessionId);
//        }
//
//        return streamingExchange;
//    }
//
//    /**
//     * 盘口订阅
//     *
//     * @param exchangeAccountSymbol
//     */
//    private void subscribeDepth(ExchangeAccountSymbol exchangeAccountSymbol, StreamingExchange streamingExchange) {
//        String interval = "step0";
//        Integer limit = 30;
//        logger.debug("robot::买卖挂单订阅 exchange:{},currencyPair:{},interval:{},limit:{}", exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol(), interval, limit);
//        subscribeDepth(exchangeAccountSymbol, exchangeAccountSymbol.getSymbol(), streamingExchange, interval, limit);
//        logger.debug("robot::买卖挂单订阅成功 exchange:{},currencyPair:{},interval:{},limit:{}", exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol(), interval, limit);
//
//    }
//
//    private void subscribeDepth(ExchangeAccountSymbol exchangeAccountSymbol, String currencyPair, StreamingExchange
//            streamingExchange, String interval, Integer limit) {
//
//        Object[] args = new Object[0];
//        //初始挂单
//        String orderBookCacheKey = genOrderBookKey(exchangeAccountSymbol);
//        if (ExchangeType.GATEIO.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            interval = DepthInterval.fromDepthIntervalCode(interval).getDepth();
//            args = new Object[]{limit, interval,true};
//        }else if (ExchangeType.OKEX.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return;
//        } else if (ExchangeType.BITFINEX.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            //bitfinex挂单全量返回true,增量false
//            args = new Object[]{25,true};
//        }else if (ExchangeType.KUCOIN.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            //初始挂单
////            OrderBook book = getOrderBook(exchangeAccountSymbol.getTradeSymbol());
//            OrderBook book = exchangeParseDataUtil.getDepth(exchangeAccountSymbol);
//            try {
//                blockvcRedisUtil.set(orderBookCacheKey.toLowerCase(), new ObjectMapper().writeValueAsString(book), 30 * 60L);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        } else {
//            interval = DepthInterval.fromDepthIntervalCode(interval).getCode();
//            args = new Object[]{interval};
//        }
//
//
//        // 订阅盘口信息并更新缓存
//        streamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair(currencyPair), args).subscribe(
//                orderBook -> {
//                    if(ExchangeType.KUCOIN.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())){
////                        orderBook = modifyOrderBook(orderBook,orderBookCacheKey.toLowerCase(),exchangeAccountSymbol);
//                        orderBook = exchangeParseDataUtil.getDepth(exchangeAccountSymbol);
//                    }
//                    if(ExchangeType.BINANCE.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())){
//                        orderBook = binanceService.adaptOrderBook(orderBook,currencyPair);
//                    }
//                    blockvcRedisUtil.set(orderBookCacheKey.toLowerCase(), new ObjectMapper().writeValueAsString(orderBook), 30 * 60L);
//                },
//                throwable -> logger.error("ERROR in getting depth: " + exchangeAccountSymbol.getExchCode() + " " + currencyPair, throwable)
//        );
//    }
//    private OrderBook modifyOrderBook(OrderBook orderBook,String orderBookCacheKey,ExchangeAccountSymbol exchangeAccountSymbol){
//        final ObjectMapper objectMapper = new ObjectMapper();
//        OrderBook redisBook = null;
//        if (blockvcRedisUtil.get(orderBookCacheKey) != null) {
//            String json = blockvcRedisUtil.get(orderBookCacheKey).toString();
//            JSONObject jsonObject = JSONObject.parseObject(json);
//            redisBook = adaptOrderBook(orderBook,jsonObject, new CurrencyPair(exchangeAccountSymbol.getSymbol()));
//        }else{
//            redisBook = exchangeParseDataUtil.getDepth(exchangeAccountSymbol);
//        }
//        return redisBook;
//    }
//
//    private OrderBook adaptOrderBook(OrderBook orderBook,JSONObject jsonObject, CurrencyPair currencyPair) {
//        List<LimitOrder> asks = Lists.newArrayList();
//        List<LimitOrder> bids = Lists.newArrayList();
//        JSONArray asksArray = jsonObject.getJSONArray("asks");
//        List<Object> asksList = asksArray.toJavaList(Object.class);
//        Collections.reverse(asksList);
//        int i = 0;
//        for (int k =0;k<asksList.size();k++) {
//            i++;
//            JSONObject item = JSON.parseObject(asksList.get(k).toString());
//                //与推送数量合并
//                if(orderBook.getAsks().size() > 0){
//                    LimitOrder limitOrder = orderBook.getAsks().get(0);
//                    if(item.getBigDecimal("limitPrice").compareTo(limitOrder.getLimitPrice()) == 0){
//                        BigDecimal amount = limitOrder.getOriginalAmount();
//                        if(amount.doubleValue() <= 0){
//                            i--;
//                            orderBook.getAsks().remove(limitOrder);
//                            continue;
//                        }else{
//                            asks.add(new LimitOrder(Order.OrderType.ASK, amount, amount, currencyPair,
//                                    String.valueOf(i), null, item.getBigDecimal("limitPrice")));
//                            orderBook.getAsks().remove(limitOrder);
//                            continue;
//                        }
//                    }else if(limitOrder.getLimitPrice().compareTo(item.getBigDecimal("limitPrice")) == -1 &&
//                            limitOrder.getOriginalAmount().compareTo(new BigDecimal(0)) != 0){
//                        asks.add(new LimitOrder(Order.OrderType.ASK, limitOrder.getOriginalAmount(), limitOrder.getOriginalAmount(), currencyPair,
//                                String.valueOf(i), null, limitOrder.getLimitPrice()));
//                        orderBook.getAsks().remove(limitOrder);
//                        i++;
//                    }
//                }
//                asks.add(new LimitOrder(Order.OrderType.ASK, item.getBigDecimal("originalAmount"), item.getBigDecimal("originalAmount"), currencyPair,
//                        String.valueOf(i), null, item.getBigDecimal("limitPrice")));
//                if(k+1 == asksList.size() && orderBook.getAsks().size() >0){
//                    LimitOrder limitOrder = orderBook.getAsks().get(0);
//                    if(limitOrder.getOriginalAmount().compareTo(new BigDecimal(0)) != 0) {
//                        asks.add(new LimitOrder(Order.OrderType.ASK, limitOrder.getOriginalAmount(), limitOrder.getOriginalAmount(), null,
//                                String.valueOf(i + 1), null, limitOrder.getLimitPrice()));
//                    }
//                }
//
//            }
//        JSONArray bidsArray = jsonObject.getJSONArray("bids");
//        List<Object> bidsList = bidsArray.toJavaList(Object.class);
//        int j = 0;
//        for (int c = 0;c<bidsList.size();c++) {
//                j++;
//            JSONObject item = JSON.parseObject(bidsList.get(c).toString());
//                if(orderBook.getBids().size() > 0){
//                    LimitOrder limitOrder = orderBook.getBids().get(0);
//                    if(item.getBigDecimal("limitPrice").compareTo(limitOrder.getLimitPrice()) == 0){
//                        BigDecimal amount = limitOrder.getOriginalAmount();
//                        if(amount.doubleValue() <= 0){
//                            j--;
//                            orderBook.getBids().remove(limitOrder);
//                            continue;
//                        }else{
//                            bids.add(new LimitOrder(Order.OrderType.BID, amount, amount, currencyPair,
//                                    String.valueOf(j), null, item.getBigDecimal("limitPrice")));
//                            orderBook.getBids().remove(limitOrder);
//                            continue;
//                        }
//                    }else if(item.getBigDecimal("limitPrice").compareTo(limitOrder.getLimitPrice()) == -1 &&
//                            limitOrder.getOriginalAmount().compareTo(new BigDecimal(0)) != 0){
//                        bids.add(new LimitOrder(Order.OrderType.BID, limitOrder.getOriginalAmount(), limitOrder.getOriginalAmount(), currencyPair,
//                                String.valueOf(j), null, limitOrder.getLimitPrice()));
//                        orderBook.getBids().remove(limitOrder);
//                        j++;
//                    }
//                }
//                bids.add(new LimitOrder(Order.OrderType.BID, item.getBigDecimal("originalAmount"), item.getBigDecimal("originalAmount"), currencyPair,
//                        String.valueOf(j), null, item.getBigDecimal("limitPrice")));
//                if(c+1 == bidsList.size() && orderBook.getBids().size() >0){
//                    LimitOrder limitOrder = orderBook.getBids().get(0);
//                    if(limitOrder.getOriginalAmount().compareTo(new BigDecimal(0)) != 0){
//                        bids.add(new LimitOrder(Order.OrderType.BID, limitOrder.getOriginalAmount(), limitOrder.getOriginalAmount(), null,
//                                String.valueOf(j+1), null, limitOrder.getLimitPrice()));
//                    }
//                }
//            }
//        return new OrderBook(null, asks, bids);
//    }
//    private String genOrderBookKey(ExchangeAccountSymbol exchangeAccountSymbol) {
//        if (ExchangeType.HUOBI.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return MessageFormat.format(Constant.HUOBI_ORDER_BOOK_KEY_FORMAT,
//                    exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbolType(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//        }else if (ExchangeType.HUOBIKO.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return MessageFormat.format(Constant.HUOBI_ORDER_BOOK_KEY_FORMAT,
//                    exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbolType(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//        }else if (ExchangeType.HUOBIEOS.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return MessageFormat.format(Constant.HUOBI_ORDER_BOOK_KEY_FORMAT,
//                    exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbolType(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//        }
//        return MessageFormat.format(Constant.ORDER_BOOK_KEY_FORMAT,
//                exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//    }
//
//    private String genTickerKey(ExchangeAccountSymbol exchangeAccountSymbol) {
//        if (ExchangeType.HUOBI.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return MessageFormat.format(Constant.HUOBI_REAL_TIME_TICKER_KEY_FORMAT,
//                    exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbolType(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//        }else if (ExchangeType.HUOBIKO.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return MessageFormat.format(Constant.HUOBI_REAL_TIME_TICKER_KEY_FORMAT,
//                    exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbolType(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//        }else if (ExchangeType.HUOBIEOS.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())) {
//            return MessageFormat.format(Constant.HUOBI_REAL_TIME_TICKER_KEY_FORMAT,
//                    exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbolType(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//        }
//        return MessageFormat.format(Constant.REAL_TIME_TICKER_KEY_FORMAT,
//                exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol()).toLowerCase();
//    }
//
//    /**
//     * 实时行情订阅
//     *
//     * @param exchangeAccountSymbol
//     */
//    private void subscribeTicker(ExchangeAccountSymbol exchangeAccountSymbol, StreamingExchange streamingExchange) {
//        logger.debug("robot::实时行情订阅 exchange:{},currencyPair:{}", exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol());
//        subscribeTicker(exchangeAccountSymbol, exchangeAccountSymbol.getSymbol().toLowerCase(), streamingExchange);
//        logger.debug("robot::实时行情订阅成功 exchange:{},currencyPair:{}", exchangeAccountSymbol.getExchCode(), exchangeAccountSymbol.getSymbol());
//    }
//
//    private void subscribeTicker(ExchangeAccountSymbol exchangeAccountSymbol, String
//            currencyPair, StreamingExchange streamingExchange) {
//        streamingExchange.getStreamingMarketDataService().getTicker(new CurrencyPair(currencyPair)).subscribe(
//                ticker -> {
//                    String tickerCacheKey = genTickerKey(exchangeAccountSymbol);
//                    logger.debug("begin set redis,key:[{}],ticker:{}", tickerCacheKey, ticker);
//                    StopWatch stopWatch = new StopWatch("Ticker");
//                    stopWatch.start("set ticker");
//                    TickerResponseDto tickerResponseDto = new TickerResponseDto();
//                    tickerResponseDto.setCurrencyPair(ticker.getCurrencyPair().toString());
//                    //okex ws未返回总额改用rest
////                    if(ExchangeType.OKEX.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())){
////                        IStockRestApi stockPost = new StockRestApi();
////                        String tickers = stockPost.ticker(new CurrencyPair(currencyPair).toString().replaceAll("/","_"));
////                        JSONObject tickerJson = JSONObject.parseObject(tickers);
////                        OkTicker okTicker = JSONObject.parseObject(tickerJson.getJSONArray("data").get(0).toString(), OkTicker.class);
////                        tickerResponseDto.setBaseVolume(okTicker.getCoinVolume());
////                    }
//                    if(ExchangeType.KUCOIN.getExchangeCode().equals(exchangeAccountSymbol.getExchCode())){
//                        TickerResponseDto responseDto = exchangeParseDataUtil.getTicker(exchangeAccountSymbol);
//                        tickerResponseDto.setBaseVolume(responseDto.getBaseVolume());
//                    }else{
//                        tickerResponseDto.setBaseVolume(ticker.getQuoteVolume().toString());
//                    }
//                    tickerResponseDto.setLow(ticker.getLow() == null?null:ticker.getLow().toString());
//                    tickerResponseDto.setHigh(ticker.getHigh() == null?null:ticker.getHigh().toString());
//                    tickerResponseDto.setLast(ticker.getLast() == null?null:ticker.getLast().toString());
//                    blockvcRedisUtil.set(Constant.TICKER_KEY + exchangeAccountSymbol.getExchAcctSymbolId(), tickerResponseDto);
//                    blockvcRedisUtil.set(tickerCacheKey.toLowerCase(), tickerResponseDto);
//                    stopWatch.stop();
//                    logger.debug("end set redis,{}", stopWatch);
//                },
//                throwable -> logger.error("ERROR in getting ticker: " + exchangeAccountSymbol.getExchCode() + " " + currencyPair, throwable));
//    }
//
//    @Scheduled(fixedDelay = 1000 * 60 * 60 * 23)
//    public void reLaunchWebsocketAgent() throws InterruptedException {
//        SessionUtil.getSessions().keySet().stream().forEach(exchangeKey -> {
//            logger.info("=============reLaunchWebsocketAgent key:{}, after {}s=============", exchangeKey, 1000 * 60 * 60 * 23);
//
//            if (SessionUtil.getSubscribes().containsKey(exchangeKey)) {
//                SessionUtil.getSubscribes().get(exchangeKey).stream().forEach(disposable -> disposable.dispose());
//            }
//            if (SessionUtil.getSessions().containsKey(exchangeKey)) {
//                SessionUtil.getSessions().get(exchangeKey).disconnect();
//                SessionUtil.getSessions().remove(exchangeKey);
//            }
//
//        });
//        Thread.sleep(3000);
//        logger.info("等待3s后 launchWebsocketAgent");
//        this.launchRobotWebsocket();
//    }
//
//    public void reLaunchWebsocketAgentByExchangeAccountSymbol(ExchangeAccountSymbol exchangeAccountSymbol){
//        logger.info("重新订阅机器人盘口及深度信息{}", exchangeAccountSymbol.getExchAcctSymbolId());
//        Map<String, String> sessionStringMap = createSessionId(exchangeAccountSymbol);
//        String sessionId = sessionStringMap.get("sessionId");
//        try {
//            if (SessionUtil.getSubscribes().containsKey(sessionId)) {
//                SessionUtil.getSubscribes().get(sessionId).stream().forEach(disposable -> disposable.dispose());
//            }
//            if (SessionUtil.getSessions().containsKey(sessionId)) {
//                SessionUtil.getSessions().get(sessionId).disconnect();
//                SessionUtil.getSessions().remove(sessionId);
//            }
//        } catch (Exception e) {
//            logger.warn("重新订阅机器人盘口及深度信息，断连接失败：", e);
//        }
//        reLaunchRobotWebsocket(exchangeAccountSymbol);
//    }

}
