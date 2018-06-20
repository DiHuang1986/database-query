package com.ce.query.exception;

/**
 * Created by dhuang on 2/23/2018.
 */
public class QueryException extends RuntimeException {

    public QueryException() {
        super();
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(String message) {
        super(message);
    }
}
