package com.rsandoval.todo_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Whenever this exception is thrown it should automatically respond with an HTTP 404 NOT FOUND status
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeException{
    // Standard constructor for an exception
    public TaskNotFoundException(String message){
        super(message);
    }
}
