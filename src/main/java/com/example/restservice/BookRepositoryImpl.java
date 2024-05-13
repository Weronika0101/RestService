package com.example.restservice;

import java.util.ArrayList;
import java.util.List;

public class BookRepositoryImpl implements BookRepository {
    private List<Book> bookList = new ArrayList<>();

    public BookRepositoryImpl() {
        // Inicjowanie przykładowymi danymi
        bookList.add(new Book(1, "Pan Tadeusz","Adam Mickiewicz", 1980));
        bookList.add(new Book(2, "Ferdydurke","Witold Gombrowicz", 2000));
    }

    @Override
    public List<Book> getAllBooks() {
        return bookList;
    }

    @Override
    public Book getBook(int id) throws BookNotFoundEx {
        System.out.print("szukamy ksiazki");
        return bookList.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundEx(id));
    }

    @Override
    public Book updateBook(Book book) throws BookNotFoundEx {
        Book existingBook = getBook(book.getId());
        existingBook.setTitle(book.getTitle());
        existingBook.setYear(book.getYear());
        return existingBook;
    }

    @Override
    public boolean deleteBook(int id) throws BookNotFoundEx {
        Book book = getBook(id);
        return bookList.remove(book);
    }

    @Override
    public Book addBook(Book book) throws BookExistsEx, BadRequestEx {
        if (book.getId() == 0 || book.getYear() == 0) {  // Assuming `id` and `age` should not be zero
            throw new BadRequestEx("Book must have a valid id and year");
        }
        if (bookList.stream().anyMatch(p -> p.getId() == book.getId())) {
            throw new BookExistsEx();
        }
        bookList.add(book);
        return book;
    }


}

