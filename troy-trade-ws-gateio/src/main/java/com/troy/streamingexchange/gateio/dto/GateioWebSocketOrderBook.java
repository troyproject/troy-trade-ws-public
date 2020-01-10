package com.troy.streamingexchange.gateio.dto;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.reverseOrder;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
@Slf4j
public class GateioWebSocketOrderBook {
    private Map<BigDecimal, GateioPublicOrder> asks;
    private Map<BigDecimal, GateioPublicOrder> bids;


    public GateioWebSocketOrderBook(GateioWebSocketOrderBookTransaction orderbookTransaction,boolean hasZero) {
        if(!hasZero){
            createFromLevels(orderbookTransaction);
        }else{
            createFromLevelsAll(orderbookTransaction);
        }
    }

    private void createFromLevels(GateioWebSocketOrderBookTransaction orderbookTransaction) {
        this.asks = Collections.synchronizedMap(new TreeMap<>(BigDecimal::compareTo));
        this.bids = Collections.synchronizedMap(new TreeMap<>(reverseOrder(BigDecimal::compareTo)));

        for (GateioPublicOrder orderBookItem : orderbookTransaction.getParams().getAsks()) {
            if (orderBookItem.getAmount().signum() != 0) {
                asks.put(orderBookItem.getPrice(), orderBookItem);
            }
        }

        for (GateioPublicOrder orderBookItem : orderbookTransaction.getParams().getBids()) {
            if (orderBookItem.getAmount().signum() != 0) {
                bids.put(orderBookItem.getPrice(), orderBookItem);
            }
        }
    }
    private void createFromLevelsAll(GateioWebSocketOrderBookTransaction orderbookTransaction) {
        this.asks = Collections.synchronizedMap(new TreeMap<>(BigDecimal::compareTo));
        this.bids = Collections.synchronizedMap(new TreeMap<>(reverseOrder(BigDecimal::compareTo)));

        for (GateioPublicOrder orderBookItem : orderbookTransaction.getParams().getAsks()) {
            asks.put(orderBookItem.getPrice(), orderBookItem);
        }

        for (GateioPublicOrder orderBookItem : orderbookTransaction.getParams().getBids()) {
            bids.put(orderBookItem.getPrice(), orderBookItem);
        }
    }

    public Map<BigDecimal, GateioPublicOrder> getAsks() {
        return asks;
    }

    public Map<BigDecimal, GateioPublicOrder> getBids() {
        return bids;
    }

    public void updateOrderBook(GateioWebSocketOrderBookTransaction orderBookTransaction) {
        if (orderBookTransaction.getParams().getResult().equals("true")) {
            return;
        }
        updateOrderBookItems(orderBookTransaction.getParams().getAsks(), asks);
        updateOrderBookItems(orderBookTransaction.getParams().getBids(), bids);
    }

    private void updateOrderBookItems(List<GateioPublicOrder> itemsToUpdate, Map<BigDecimal, GateioPublicOrder> localItems) {
        for (GateioPublicOrder itemToUpdate : itemsToUpdate) {
            localItems.remove(itemToUpdate.getPrice());
            if (itemToUpdate.getAmount().signum() != 0) {
                localItems.put(itemToUpdate.getPrice(), itemToUpdate);
            }
        }
    }
}
