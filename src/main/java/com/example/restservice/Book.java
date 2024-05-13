package com.example.restservice;
enum BookStatus {
    AVAILABLE,
    NOT_AVAILABLE,
    BORROWED
}

public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private BookStatus status;

    public Book(int id, String tytul,String autor, int year) {
        this.id = id;
        this.title = tytul;
        this.author = autor;
        this.year = year;
        this.status = BookStatus.AVAILABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
