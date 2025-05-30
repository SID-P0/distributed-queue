package com.backend.distributedqueue.exception;

public class TaskActivityException extends RuntimeException{
    public TaskActivityException(String message) {
        super(message);
    }

    public TaskActivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
