package com.example.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BookNotFoundEx extends RuntimeException {
    public BookNotFoundEx() {
        super("The specified book does not exist");
    }
    public BookNotFoundEx(int id) {
        super(String.valueOf(id));
    }
}
