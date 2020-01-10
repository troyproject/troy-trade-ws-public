package com.troy.streamingexchange.bitfinex.dto;

import com.troy.streamingexchange.bitfinex.dto.marketdata.BitfinexDepth;
import com.troy.streamingexchange.bitfinex.dto.marketdata.BitfinexLevel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ZERO;

public class BitfinexOrderbook implements Serializable {

    /**
     * @Fields serialVersionUID TODO（描述变量的含义）
     */
    private static final long serialVersionUID = 1229870442710833570L;

    private Map<BigDecimal, BitfinexOrderbookLevel> asks;
    private Map<BigDecimal, BitfinexOrderbookLevel> bids;

    public BitfinexOrderbook(BitfinexOrderbookLevel[] levels) {
        createFromLevels(levels);
    }

    private void createFromLevels(BitfinexOrderbookLevel[] levels) {
        this.asks = new HashMap<>(levels.length / 2);
        this.bids = new HashMap<>(levels.length / 2);

        for (BitfinexOrderbookLevel level : levels) {

            if(level.getCount().compareTo(ZERO) == 0)
                continue;

            if (level.getAmount().compareTo(ZERO) > 0)
                bids.put(level.getPrice(), level);
            else
                asks.put(level.getPrice(),
                        new BitfinexOrderbookLevel(
                        level.getPrice(),
                        level.getCount(),
                        level.getAmount().abs()
                ));
        }
    }

    public BitfinexDepth toBitfinexDepth() {
        SortedMap<BigDecimal, BitfinexOrderbookLevel> bitfinexLevelAsks = new TreeMap<>();
        SortedMap<BigDecimal, BitfinexOrderbookLevel> bitfinexLevelBids = new TreeMap<>(java.util.Collections.reverseOrder());

        for (Map.Entry<BigDecimal, BitfinexOrderbookLevel> level : asks.entrySet()) {
            bitfinexLevelAsks.put(level.getValue().getPrice(), level.getValue());
        }

        for (Map.Entry<BigDecimal, BitfinexOrderbookLevel> level : bids.entrySet()) {
            bitfinexLevelBids.put(level.getValue().getPrice(), level.getValue());
        }

        List<BitfinexLevel> askLevels = new ArrayList<>(asks.size());
        List<BitfinexLevel> bidLevels = new ArrayList<>(bids.size());
        for (Map.Entry<BigDecimal, BitfinexOrderbookLevel> level : bitfinexLevelAsks.entrySet()) {
            askLevels.add(level.getValue().toBitfinexLevel());
        }
        for (Map.Entry<BigDecimal, BitfinexOrderbookLevel> level : bitfinexLevelBids.entrySet()) {
            bidLevels.add(level.getValue().toBitfinexLevel());
        }

        return new BitfinexDepth(askLevels.toArray(new BitfinexLevel[askLevels.size()]),
                bidLevels.toArray(new BitfinexLevel[bidLevels.size()]));
    }

    public void updateLevel(BitfinexOrderbookLevel level) {


        Map<BigDecimal, BitfinexOrderbookLevel> side;

        // Determine side and normalize negative ask amount values
        BitfinexOrderbookLevel bidAskLevel = level;
        if(level.getAmount().compareTo(ZERO) < 0) {
            side = asks;
            bidAskLevel = new BitfinexOrderbookLevel(
                    level.getPrice(),
                    level.getCount(),
                    level.getAmount().abs()
            );
        } else {
            side = bids;
        }

        boolean shouldDelete = bidAskLevel.getCount().compareTo(ZERO) == 0;

        side.remove(bidAskLevel.getPrice());
        if (!shouldDelete) {
            side.put(bidAskLevel.getPrice(), bidAskLevel);
        }
    }

    public void pushLevel(BitfinexOrderbookLevel level) {

        // Determine side and normalize negative ask amount values
        bids = new HashMap<>();
        asks = new HashMap<>();

        //when count > 0 then you have to add or update the price level
        //when count = 0 then you have to delete the price level.

        BigDecimal amount = level.getAmount().doubleValue() != -1 ?
                level.getAmount().abs() : level.getAmount();
        if(level.getCount().compareTo(BigDecimal.ZERO)==0){
            amount = BigDecimal.ZERO;
        }
        BitfinexOrderbookLevel bidAskLevel = new BitfinexOrderbookLevel(
                level.getPrice(),
                level.getCount(),
                amount
        );
        String type = null;
        if(level.getCount().compareTo(ZERO) > 0){
            if(level.getAmount().compareTo(ZERO) < 0) {
                type = "asks";

            } else if(level.getAmount().compareTo(ZERO) > 0){
                type = "bids";
            }

        }else if(level.getCount().compareTo(ZERO) == 0){
            if(level.getAmount().compareTo(ZERO) == 1) {
                type = "bids";
            } else if(level.getAmount().compareTo(ZERO) == -1){
                type = "asks";
            }
        }

        if(type.equals("bids")){
            bids.put(bidAskLevel.getPrice(), bidAskLevel);
        }else{
            asks.put(bidAskLevel.getPrice(), bidAskLevel);
        }
    }
}
