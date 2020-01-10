package com.troy.trade.ws.streamingexchange.core;

import com.troy.commons.utils.Assert;
import com.troy.trade.ws.exceptions.ExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Factory to provide the following to {@link StreamingExchange}:
 * </p>
 * <ul>
 * <li>Manages the creation of specific Exchange implementations using runtime dependencies</li>
 * </ul>
 */
public enum StreamingExchangeFactory {

    INSTANCE;

    // flags
    private final Logger LOG = LoggerFactory.getLogger(StreamingExchangeFactory.class);

    /**
     * Constructor
     */
    private StreamingExchangeFactory() {

    }

    /**
     * Create an Exchange object without default ExchangeSpecification
     * <p>
     * The factory is parameterised with the name of the exchange implementation class. This must be a class extending
     * </p>
     *
     * @param exchangeClassName the fully-qualified class name of the exchange
     * @return a new exchange instance configured with the default
     */
    public StreamingExchange createExchangeWithoutSpecification(String exchangeClassName) {

        Assert.notNull(exchangeClassName, "exchangeClassName cannot be null");

        LOG.debug("Creating default exchange from class name");

        // Attempt to create an instance of the exchange provider
        try {

            // Attempt to locate the exchange provider on the classpath
            Class exchangeProviderClass = Class.forName(exchangeClassName);

            // Test that the class implements Exchange
            if (StreamingExchange.class.isAssignableFrom(exchangeProviderClass)) {
                // Instantiate through the default constructor and use the default exchange specification
                StreamingExchange exchange = (StreamingExchange) exchangeProviderClass.newInstance();
                return exchange;
            } else {
                throw new ExchangeException("Class '" + exchangeClassName + "' does not implement Exchange");
            }
        } catch (ClassNotFoundException e) {
            throw new ExchangeException("Problem creating Exchange (class not found)", e);
        } catch (InstantiationException e) {
            throw new ExchangeException("Problem creating Exchange (instantiation)", e);
        } catch (IllegalAccessException e) {
            throw new ExchangeException("Problem creating Exchange (illegal access)", e);
        }

        // Cannot be here due to exceptions

    }

    /**
     * Create an Exchange object with default ExchangeSpecification
     * <p>
     * The factory is parameterised with the name of the exchange implementation class. This must be a class extending
     * </p>
     *
     * @param exchangeClassName the fully-qualified class name of the exchange
     * @return a new exchange instance configured with the default
     */
    public StreamingExchange createExchange(String exchangeClassName) {

        Assert.notNull(exchangeClassName, "exchangeClassName cannot be null");

        LOG.debug("Creating default exchange from class name");

        StreamingExchange exchange = createExchangeWithoutSpecification(exchangeClassName);
        exchange.applyData();
        return exchange;

    }

//    public StreamingExchange createExchange(ExchangeSpecification exchangeSpecification) {
//
//        Assert.notNull(exchangeSpecification, "exchangeSpecfication cannot be null");
//
//        LOG.debug("Creating exchange from specification");
//
//        String exchangeClassName = exchangeSpecification.getExchangeClassName();
//
//        // Attempt to create an instance of the exchange provider
//        try {
//
//            // Attempt to locate the exchange provider on the classpath
//            Class exchangeProviderClass = Class.forName(exchangeClassName);
//
//            // Test that the class implements Exchange
//            if (Exchange.class.isAssignableFrom(exchangeProviderClass)) {
//                // Instantiate through the default constructor
//                StreamingExchange exchange = (StreamingExchange) exchangeProviderClass.newInstance();
//                exchange.applySpecification(exchangeSpecification);
//                return exchange;
//            } else {
//                throw new ExchangeException("Class '" + exchangeClassName + "' does not implement Exchange");
//            }
//        } catch (ClassNotFoundException e) {
//            throw new ExchangeException("Problem starting exchange provider (class not found)", e);
//        } catch (InstantiationException e) {
//            throw new ExchangeException("Problem starting exchange provider (instantiation)", e);
//        } catch (IllegalAccessException e) {
//            throw new ExchangeException("Problem starting exchange provider (illegal access)", e);
//        }
//
//        // Cannot be here due to exceptions
//
//    }

}
