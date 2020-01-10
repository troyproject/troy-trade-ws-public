package com.troy.trade.ws.dto.currency;

public class CurrencyPair implements Comparable<CurrencyPair>  {
    public String baseSymbol;
    public String counterSymbol;

    /**
     * <p>
     * String constructor
     * </p>
     * In general the CurrencyPair.base is what you're wanting to buy/sell. The CurrencyPair.counter is what currency you want to use to pay/receive for
     * your purchase/sale.
     *
     * @param baseSymbol The base symbol is what you're wanting to buy/sell
     * @param counterSymbol The counter symbol is what currency you want to use to pay/receive for your purchase/sale.
     */
    public CurrencyPair(String baseSymbol, String counterSymbol) {
        this.baseSymbol = baseSymbol;
        this.counterSymbol = counterSymbol;
    }

    /**
     * Parse currency pair from a string in the same format as returned by toString() method - ABC/XYZ
     */
    public CurrencyPair(String currencyPair) {
        int split = currencyPair.indexOf("/");
        if (split < 1) {
            throw new IllegalArgumentException("Could not parse currency pair from '" + currencyPair + "'");
        }
        this.baseSymbol = currencyPair.substring(0, split);
        this.counterSymbol = currencyPair.substring(split + 1);
    }

    @Override
    public String toString() {
        return baseSymbol + "/" + counterSymbol;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseSymbol == null) ? 0 : baseSymbol.hashCode());
        result = prime * result + ((counterSymbol == null) ? 0 : counterSymbol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CurrencyPair other = (CurrencyPair) obj;
        if (baseSymbol == null) {
            if (other.baseSymbol != null) {
                return false;
            }
        } else if (!baseSymbol.equals(other.baseSymbol)) {
            return false;
        }
        if (counterSymbol == null) {
            if (other.counterSymbol != null) {
                return false;
            }
        } else if (!counterSymbol.equals(other.counterSymbol)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CurrencyPair o) {
        if(baseSymbol.compareTo(o.baseSymbol) == 0 && counterSymbol.compareTo(o.counterSymbol) == 0){
            return 0;
        }else{
            return -1;
        }
    }
}
