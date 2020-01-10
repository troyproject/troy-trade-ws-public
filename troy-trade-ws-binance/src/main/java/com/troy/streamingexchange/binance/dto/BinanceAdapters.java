package com.troy.streamingexchange.binance.dto;

import com.troy.streamingexchange.binance.dto.enums.BinanceOrderStatusEnum;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderSideEnum;
import com.troy.trade.ws.enums.OrderStatusEnum;
import com.troy.trade.ws.enums.OrderTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class BinanceAdapters {

    private BinanceAdapters() {}

    public static String toSymbol(CurrencyPair pair) {
        return pair.baseSymbol + pair.counterSymbol;
    }

    public static OrderTypeEnum convert(OrderSideEnum side) {
        switch (side) {
            case BUY:
                return OrderTypeEnum.BID;
            case SELL:
                return OrderTypeEnum.ASK;
            default:
                throw new RuntimeException("Not supported order side: " + side);
        }
    }

    public static OrderSideEnum convert(OrderTypeEnum type) {
        switch (type) {
            case ASK:
                return OrderSideEnum.SELL;
            case BID:
                return OrderSideEnum.BUY;
            default:
                throw new RuntimeException("Not supported order type: " + type);
        }
    }

    public static long id(String id) {
        try {
            return Long.valueOf(id);
        } catch (Throwable e) {
            throw new RuntimeException("Binance id must be a valid long number.", e);
        }
    }

    public static OrderStatusEnum adaptOrderStatus(BinanceOrderStatusEnum orderStatus) {
        switch (orderStatus) {
            case NEW:
                return OrderStatusEnum.NEW;
            case FILLED:
                return OrderStatusEnum.FILLED;
            case EXPIRED:
                return OrderStatusEnum.EXPIRED;
            case CANCELED:
                return OrderStatusEnum.CANCELED;
            case REJECTED:
                return OrderStatusEnum.REJECTED;
            case PENDING_CANCEL:
                return OrderStatusEnum.PENDING_CANCEL;
            case PARTIALLY_FILLED:
                return OrderStatusEnum.PARTIALLY_FILLED;
            default:
                return OrderStatusEnum.UNKNOWN;
        }
    }

    public static OrderTypeEnum convertType(boolean isBuyer) {
        return isBuyer ? OrderTypeEnum.BID : OrderTypeEnum.ASK;
    }

    public static CurrencyPair adaptSymbol(String tradeSymbol) {
        // BTCUSDT
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        String baseSymbol;
        String counterSymbol;
        // BNB、BTC、ETH、XRP、PAX、USDT、TUSD、USDC、USDS、BUSD、NGN、TRX
        if (tradeSymbol.endsWith("BNB")
                || tradeSymbol.endsWith("BTC")
                || tradeSymbol.endsWith("ETH")
                || tradeSymbol.endsWith("XRP")
                || tradeSymbol.endsWith("PAX")
                || tradeSymbol.endsWith("NGN")
                || tradeSymbol.endsWith("TRX")) {
            baseSymbol = tradeSymbol.substring(0, tradeSymbol.length() - 3);
            counterSymbol = tradeSymbol.substring(tradeSymbol.length() - 3);
        } else {
            baseSymbol = tradeSymbol.substring(0, tradeSymbol.length() - 4);
            counterSymbol = tradeSymbol.substring(tradeSymbol.length() - 4);
        }
        return new CurrencyPair(baseSymbol,counterSymbol);
    }

//    public static Order adaptOrder(BinanceOrder order) {
//        OrderTypeEnum type = convert(order.side);
//        CurrencyPair currencyPair = adaptSymbol(order.symbol);
//
//        OrderStatusEnum orderStatus = adaptOrderStatus(order.status);
//        final BigDecimal averagePrice;
//        if (order.executedQty.signum() == 0
//                || order.type.equals(org.knowm.xchange.binance.dto.trade.OrderType.MARKET)) {
//            averagePrice = BigDecimal.ZERO;
//        } else {
//            averagePrice = order.price;
//        }
//
//        Order result;
//        if (order.type.equals(org.knowm.xchange.binance.dto.trade.OrderType.MARKET)) {
//            result =
//                    new MarketOrder(
//                            type,
//                            order.origQty,
//                            currencyPair,
//                            Long.toString(order.orderId),
//                            order.getTime(),
//                            averagePrice,
//                            order.executedQty,
//                            BigDecimal.ZERO,
//                            orderStatus);
//        } else if (order.type.equals(org.knowm.xchange.binance.dto.trade.OrderType.LIMIT)) {
//            result =
//                    new LimitOrder(
//                            type,
//                            order.origQty,
//                            currencyPair,
//                            Long.toString(order.orderId),
//                            order.getTime(),
//                            order.price,
//                            averagePrice,
//                            order.executedQty,
//                            BigDecimal.ZERO,
//                            orderStatus);
//        } else {
//            result =
//                    new StopOrder(
//                            type,
//                            order.origQty,
//                            currencyPair,
//                            Long.toString(order.orderId),
//                            order.getTime(),
//                            order.stopPrice,
//                            averagePrice,
//                            order.executedQty,
//                            orderStatus);
//        }
//        result.setOrderFlags(flags);
//        return result;
//    }
}
