package com.troy.trade.ws.streamingexchange.core;

import com.troy.trade.ws.dto.currency.CurrencyPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Use to specify subscriptions during the connect phase
 * For instancing, use builder @link {@link ProductSubscriptionBuilder}
 */
public class ProductSubscription {
    private List<CurrencyPair> orderBook;
    private List<CurrencyPair> trades;
    private List<CurrencyPair> ticker;
    private boolean isRobot;

    private ProductSubscription(ProductSubscriptionBuilder builder) {
        this.orderBook = builder.orderBook;
        this.trades = builder.trades;
        this.ticker = builder.ticker;
        this.isRobot = builder.isRobot;
    }
    public boolean getIsRobot(){
        return isRobot;
    }
    public List<CurrencyPair> getOrderBook() {
        return orderBook;
    }

    public List<CurrencyPair> getTrades() {
        return trades;
    }

    public List<CurrencyPair> getTicker() {
        return ticker;
    }

    public static ProductSubscriptionBuilder create() {
        return new ProductSubscriptionBuilder();
    }

    public static class ProductSubscriptionBuilder {
        private List<CurrencyPair> orderBook;
        private List<CurrencyPair> trades;
        private List<CurrencyPair> ticker;
        private boolean isRobot;

        private ProductSubscriptionBuilder() {
            orderBook = new ArrayList<>();
            trades = new ArrayList<>();
            ticker = new ArrayList<>();
            isRobot = false;
        }

        public ProductSubscriptionBuilder addOrderbook(CurrencyPair pair) {
            orderBook.add(pair);
            return this;
        }

        public ProductSubscriptionBuilder addRobot(boolean robot) {
            isRobot = robot;
            return this;
        }

        public ProductSubscriptionBuilder addTrades(CurrencyPair pair) {
            trades.add(pair);
            return this;
        }

        public ProductSubscriptionBuilder addTicker(CurrencyPair pair) {
            ticker.add(pair);
            return this;
        }

        public ProductSubscriptionBuilder addAll(CurrencyPair pair) {
            orderBook.add(pair);
            trades.add(pair);
            ticker.add(pair);
            return this;
        }

        public ProductSubscription build() {
            return new ProductSubscription(this);
        }
    }
}