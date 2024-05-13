package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictEx extends RuntimeException {
    public ConflictEx(String message) {
        super(message);
    }
}

