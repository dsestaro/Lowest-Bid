package com.br.sestaro.lowerbid.bid.exceptions;

public class NoBidsException extends RuntimeException {
    public NoBidsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
