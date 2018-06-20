package com.ce.query.exception;

public class ConnectionFailException extends RuntimeException {

    public ConnectionFailException() {
        super();
    }

    public ConnectionFailException(Throwable cause) {
        super(cause);
    }
}
