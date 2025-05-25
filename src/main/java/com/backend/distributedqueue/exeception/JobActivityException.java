package com.backend.distributedqueue.exeception;

public class JobActivityException extends RuntimeException {
    public JobActivityException(String message) {
        super(message);
    }

    public JobActivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
