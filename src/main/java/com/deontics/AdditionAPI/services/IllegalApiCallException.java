package com.deontics.AdditionAPI.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalApiCallException extends Exception {

    public IllegalApiCallException(String errorMessage) {
        super(errorMessage);
    }
}
