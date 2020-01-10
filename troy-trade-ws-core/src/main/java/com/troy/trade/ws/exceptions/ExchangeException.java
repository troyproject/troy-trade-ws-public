package com.troy.trade.ws.exceptions;

public class ExchangeException extends RuntimeException {

    /**
     * Constructs an <code>ExchangeException</code> with the specified detail message.
     *
     * @param message the detail message.
     */
    public ExchangeException(String message) {

        super(message);
    }

    /**
     * Constructs an <code>ExchangeException</code> with the specified cause.
     *
     * @param cause the underlying cause.
     */
    public ExchangeException(Throwable cause) {

        super(cause);
    }

    /**
     * Constructs an <code>ExchangeException</code> with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the underlying cause.
     */
    public ExchangeException(String message, Throwable cause) {

        super(message, cause);
    }
}
