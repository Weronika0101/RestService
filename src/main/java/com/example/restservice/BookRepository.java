package com.example.restservice;

import java.util.List;

public interface BookRepository {
    List<Book> getAllBooks();
    Book getBook(int id) throws BookNotFoundEx;
    Book updateBook(Book book) throws BookNotFoundEx;
    boolean deleteBook(int id) throws BookNotFoundEx;
    Book addBook(Book book) throws BookExistsEx;
}

