package com.troy.streamingexchange.huobi.dto;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.reverseOrder;

/**
 * Created by Lukas Zaoralek on 8.11.17.
 */
public class HuobiOrderbook {
    private Map<BigDecimal, HuobiOrderbookLevel> asks;
    private Map<BigDecimal, HuobiOrderbookLevel> bids;

    public void createFromLevels(HuobiWebSocketUpdateOrderbook orderbookTransaction) {
        this.asks = Collections.synchronizedMap(new TreeMap<>(BigDecimal::compareTo));
        this.bids = Collections.synchronizedMap(new TreeMap<>(reverseOrder(BigDecimal::compareTo)));

        for (HuobiOrderbookLevel askOrderBookItem : orderbookTransaction.getAsks()) {
            if (askOrderBookItem.getAmount().signum() != 0) {
                asks.put(askOrderBookItem.getPrice(), askOrderBookItem);
            }
        }

        for (HuobiOrderbookLevel bidOrderBookItem : orderbookTransaction.getBids()) {
            if (bidOrderBookItem.getAmount().signum() != 0) {
                bids.put(bidOrderBookItem.getPrice(), bidOrderBookItem);
            }
        }
    }

    public Map<BigDecimal, HuobiOrderbookLevel> getAsks() {
        return asks;
    }

    public Map<BigDecimal, HuobiOrderbookLevel> getBids() {
        return bids;
    }

    public void updateOrderBook(HuobiWebSocketOrderbookTransaction orderBookTransaction) {
        updateOrderBookItems(orderBookTransaction.getAsks(), asks);
        updateOrderBookItems(orderBookTransaction.getBids(), bids);
    }

    private void updateOrderBookItems(HuobiOrderbookLevel[] itemsToUpdate, Map<BigDecimal, HuobiOrderbookLevel> localItems) {
        for (HuobiOrderbookLevel itemToUpdate : itemsToUpdate) {
            localItems.remove(itemToUpdate.getPrice());
            if (itemToUpdate.getAmount().signum() != 0) {
                localItems.put(itemToUpdate.getPrice(), itemToUpdate);
            }
        }
    }
}
