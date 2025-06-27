package com.backend.distributedqueue.exception;

public class TaskActivityException extends RuntimeException {
    public TaskActivityException(String message) {
        super(message);
    }

    public TaskActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskActivityException(String message, Object... args) {
        super(String.format(message, args));
    }
}
