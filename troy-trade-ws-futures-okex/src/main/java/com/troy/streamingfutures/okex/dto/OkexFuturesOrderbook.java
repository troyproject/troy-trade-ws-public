package com.troy.streamingfutures.okex.dto;

import com.google.common.collect.Maps;
import com.troy.streamingfutures.okex.dto.marketdata.OkexFuturesDepth;
import com.troy.trade.ws.enums.OrderTypeEnum;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Lukas Zaoralek on 16.11.17.
 */
public class OkexFuturesOrderbook {
    private final BigDecimal zero = new BigDecimal(0);

    private SortedMap<BigDecimal, BigDecimal[]> asks;
    private SortedMap<BigDecimal, BigDecimal[]> bids;

    public OkexFuturesOrderbook() {
        asks = new TreeMap<>(java.util.Collections.reverseOrder()); //Because okcoin adapter uses reverse sort for asks!!!
        bids = new TreeMap<>();
    }

    public OkexFuturesOrderbook(OkexFuturesDepth depth) {
        this();
        createFromDepth(depth);
    }

    public void createFromDepth(OkexFuturesDepth depth) {
        BigDecimal[][] depthAsks = depth.getAsks();
        BigDecimal[][] depthBids = depth.getBids();

        createFromDepthLevels(depthAsks, OrderTypeEnum.ASK);
        createFromDepthLevels(depthBids, OrderTypeEnum.BID);
    }

    public void createFromDepthLevels(BigDecimal[][] depthLevels, OrderTypeEnum side) {
        SortedMap<BigDecimal, BigDecimal[]> orderbookLevels = side == OrderTypeEnum.ASK ? asks : bids;
        for (BigDecimal[] level : depthLevels) {
            orderbookLevels.put(level[0], level);
        }
    }

    public void updateLevels(BigDecimal[][] depthLevels, OrderTypeEnum side,boolean isRobot) {
        //改成增量模式 清空asks,bids
        if(!isRobot){//是否为机器人订阅
            if(side == OrderTypeEnum.ASK){
                asks = Maps.newTreeMap();
            }else{
                bids = Maps.newTreeMap();
            }
        }
        for (BigDecimal[] level : depthLevels) {
            updateLevelSpecified(level, side,isRobot);
        }
    }

    private void updateLevelSpecified(BigDecimal[] level, OrderTypeEnum side,boolean isRobot) {
        SortedMap<BigDecimal, BigDecimal[]> orderBookSide = side == OrderTypeEnum.ASK ? asks : bids;
        BigDecimal price = level[0];
        if(isRobot){
            boolean shouldDelete = level[1].compareTo(zero) == 0;
            orderBookSide.remove(price);
            if (!shouldDelete) {
                orderBookSide.put(price, level);
            }
        }else{
            orderBookSide.put(price, level);
        }
    }

    public void updateLevel(BigDecimal[] level, OrderTypeEnum side) {
        SortedMap<BigDecimal, BigDecimal[]> orderBookSide = side == OrderTypeEnum.ASK ? asks : bids;
        boolean shouldDelete = level[1].compareTo(zero) == 0;
        BigDecimal price = level[0];
        orderBookSide.remove(price);
        if (!shouldDelete) {
            orderBookSide.put(price, level);
        }
    }

    public BigDecimal[][] getSide(OrderTypeEnum side) {
        SortedMap<BigDecimal, BigDecimal[]> orderbookLevels = side == OrderTypeEnum.ASK ? asks : bids;
        Collection<BigDecimal[]> levels = orderbookLevels.values();
        return levels.toArray(new BigDecimal[orderbookLevels.size()][]);
    }

    public BigDecimal[][] getAsks() {
        return getSide(OrderTypeEnum.ASK);
    }

    public BigDecimal[][] getBids() {
        return getSide(OrderTypeEnum.BID);
    }

    public OkexFuturesDepth toOkexFuturesDepth(String instrumentId,String checksum) {
        return new OkexFuturesDepth(getAsks(), getBids(), null,instrumentId,checksum);

    }
}
