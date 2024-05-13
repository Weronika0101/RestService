package com.example.restservice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice  // Ta adnotacja pozwala globalnie obsługiwać wyjątki dla wszystkich kontrolerów
public class FaultController {

    @ExceptionHandler(BookNotFoundEx.class)
    public ResponseEntity<?> PNFEHandler(BookNotFoundEx e) {
        Problem problem = Problem.builder()
                .withStatus(Status.NOT_FOUND)
                .withTitle(HttpStatus.NOT_FOUND.getReasonPhrase())
                .withDetail("Person with ID=" + e.getMessage() + " does not exist")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(BookExistsEx.class)
    public ResponseEntity<?> handlePersonExists(BookExistsEx e) {
        Problem problem = Problem.builder()
                .withStatus(Status.BAD_REQUEST)
                .withTitle(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .withDetail("This book already exists")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(ConflictEx.class)
    public ResponseEntity<?> ConflictHandler(ConflictEx e) {
        Problem problem = Problem.builder()
                .withStatus(Status.CONFLICT)
                .withTitle("CONFLICT")
                .withDetail(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .body(problem);
    }

    @ExceptionHandler(BadRequestEx.class)
    public ResponseEntity<String> handleBadRequest(BadRequestEx e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

}

