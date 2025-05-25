package com.backend.distributedqueue.exeception;

public class TaskActivityException extends RuntimeException{
    public TaskActivityException(String message) {
        super(message);
    }

    public TaskActivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
