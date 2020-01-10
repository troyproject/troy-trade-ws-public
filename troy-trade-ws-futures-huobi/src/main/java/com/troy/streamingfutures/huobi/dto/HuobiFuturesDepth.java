package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;

public class HuobiFuturesDepth {

    private final long id;
    public final SortedMap<BigDecimal, BigDecimal> bids;
    public final SortedMap<BigDecimal, BigDecimal> asks;

    @JsonCreator
    public HuobiFuturesDepth(@JsonProperty("id") long id,
                             @JsonProperty("bids") List<BigDecimal[]> bidsJson,
                             @JsonProperty("asks") List<BigDecimal[]> asksJson) {
        this.id = id;

        BiConsumer<BigDecimal[], Map<BigDecimal, BigDecimal>> entryProcessor =
                (obj, col) -> {
                    col.put(obj[0], obj[0]);
                };

        TreeMap<BigDecimal, BigDecimal> bids = new TreeMap<>((k1, k2) -> -k1.compareTo(k2));
        TreeMap<BigDecimal, BigDecimal> asks = new TreeMap<>();

        bidsJson.stream().forEach(e -> entryProcessor.accept(e, bids));
        asksJson.stream().forEach(e -> entryProcessor.accept(e, asks));

        this.bids = Collections.unmodifiableSortedMap(bids);
        this.asks = Collections.unmodifiableSortedMap(asks);
    }
//    public static HuobiFuturesDepth getInstance(long id,List<List> bidsJson, List<List> asksJson){
//        return new HuobiFuturesDepth(id,bidsJson, asksJson);
//    }

    public long getId() {
        return id;
    }

    public SortedMap<BigDecimal, BigDecimal> getBids() {
        return bids;
    }

    public SortedMap<BigDecimal, BigDecimal> getAsks() {
        return asks;
    }

    @Override
    public String toString() {
        return "HuobiTicker [id="
                + getId()
                + ", bids="
                + getBids().toString()
                + ", asks="
                + getAsks().toString()
                + "]";
    }
}