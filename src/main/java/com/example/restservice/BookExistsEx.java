package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BookExistsEx extends Exception {
    public BookExistsEx() {
        super("This book already exists");
    }
}
