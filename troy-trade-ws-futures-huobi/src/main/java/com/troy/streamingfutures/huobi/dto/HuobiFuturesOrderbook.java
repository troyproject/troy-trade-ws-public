package com.troy.streamingfutures.huobi.dto;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.reverseOrder;

public class HuobiFuturesOrderbook {
    private Map<BigDecimal, HuobiFuturesOrderbookLevel> asks;
    private Map<BigDecimal, HuobiFuturesOrderbookLevel> bids;

    public void createFromLevels(HuobiWebSocketUpdateOrderbook orderbookTransaction) {
        this.asks = Collections.synchronizedMap(new TreeMap<>(BigDecimal::compareTo));
        this.bids = Collections.synchronizedMap(new TreeMap<>(reverseOrder(BigDecimal::compareTo)));

        for (HuobiFuturesOrderbookLevel askOrderBookItem : orderbookTransaction.getAsks()) {
            if (askOrderBookItem.getAmount().signum() != 0) {
                asks.put(askOrderBookItem.getPrice(), askOrderBookItem);
            }
        }

        for (HuobiFuturesOrderbookLevel bidOrderBookItem : orderbookTransaction.getBids()) {
            if (bidOrderBookItem.getAmount().signum() != 0) {
                bids.put(bidOrderBookItem.getPrice(), bidOrderBookItem);
            }
        }
    }

    public Map<BigDecimal, HuobiFuturesOrderbookLevel> getAsks() {
        return asks;
    }

    public Map<BigDecimal, HuobiFuturesOrderbookLevel> getBids() {
        return bids;
    }

    public void updateOrderBook(HuobiFuturesOrderbookTransaction orderBookTransaction) {
        updateOrderBookItems(orderBookTransaction.getAsks(), asks);
        updateOrderBookItems(orderBookTransaction.getBids(), bids);
    }

    private void updateOrderBookItems(HuobiFuturesOrderbookLevel[] itemsToUpdate, Map<BigDecimal, HuobiFuturesOrderbookLevel> localItems) {
        for (HuobiFuturesOrderbookLevel itemToUpdate : itemsToUpdate) {
            localItems.remove(itemToUpdate.getPrice());
            if (itemToUpdate.getAmount().signum() != 0) {
                localItems.put(itemToUpdate.getPrice(), itemToUpdate);
            }
        }
    }
}
