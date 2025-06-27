package com.backend.distributedqueue.exception;

public class JobActivityException extends RuntimeException {
    public JobActivityException(String message) {
        super(message);
    }

    public JobActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobActivityException(String message, Object... args) {
        super(String.format(message, args));
    }

}
