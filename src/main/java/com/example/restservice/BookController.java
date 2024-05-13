package com.example.restservice;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "http://localhost:63342", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
@RestController
@RequestMapping("/books")
public class BookController {
    private BookRepository dataRepo = new BookRepositoryImpl();

    @GetMapping("/")
    public CollectionModel<EntityModel<Book>> getAllBooks() {
        List<EntityModel<Book>> books = dataRepo.getAllBooks().stream()
                .map(book -> {
                    EntityModel<Book> bookModel = EntityModel.of(book,
                            linkTo(methodOn(BookController.class).getBook(book.getId())).withSelfRel(),
                            linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all"));

                    if (book.getStatus() == BookStatus.AVAILABLE) {
                        bookModel.add(linkTo(methodOn(BookController.class).borrowBook(book.getId())).withRel("borrow"));
                        bookModel.add(linkTo(methodOn(BookController.class).deactivateBook(book.getId())).withRel("deactivate"));
                    }
                    if (book.getStatus() == BookStatus.BORROWED) {
                        bookModel.add(linkTo(methodOn(BookController.class).returnBook(book.getId())).withRel("return"));
                    }
                    if (book.getStatus() != BookStatus.BORROWED) {
                        bookModel.add(linkTo(methodOn(BookController.class).deleteBook(book.getId())).withRel("delete"));
                    }
                    if (book.getStatus() == BookStatus.NOT_AVAILABLE) {
                        bookModel.add(linkTo(methodOn(BookController.class).activateBook(book.getId())).withRel("activate"));
                    }
                    return bookModel;
                })
                .collect(Collectors.toList());

        return CollectionModel.of(books, linkTo(methodOn(BookController.class).getAllBooks()).withSelfRel());
    }


    @GetMapping("/{id}")
    public EntityModel<Book> getBook(@PathVariable int id) {
        Book book = dataRepo.getBook(id);
        EntityModel<Book> bookModel = EntityModel.of(book,
                linkTo(methodOn(BookController.class).getBook(id)).withSelfRel(),
                linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all"));

        if (book.getStatus() == BookStatus.AVAILABLE) {
            bookModel.add(linkTo(methodOn(BookController.class).borrowBook(id)).withRel("borrow"));
            bookModel.add(linkTo(methodOn(BookController.class).deactivateBook(id)).withRel("deactivate"));
        }
        if (book.getStatus() == BookStatus.BORROWED) {
            bookModel.add(linkTo(methodOn(BookController.class).returnBook(id)).withRel("return"));
        }
        if (book.getStatus() == BookStatus.NOT_AVAILABLE) {
            bookModel.add(linkTo(methodOn(BookController.class).activateBook(id)).withRel("activate"));
        }
        if (book.getStatus() != BookStatus.BORROWED) {
            bookModel.add(linkTo(methodOn(BookController.class).deleteBook(id)).withRel("delete"));
        }
        return bookModel;
    }


    @PostMapping("/")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            Book savedBook = dataRepo.addBook(book);
            EntityModel<Book> entityModel = EntityModel.of(savedBook,
                    linkTo(methodOn(BookController.class).getBook(savedBook.getId())).withSelfRel(),
                    linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all"));
            return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        } catch (BookExistsEx e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id, @RequestBody Book book) {
        try {
            book.setId(id);
            Book updatedBook = dataRepo.updateBook(book);
            EntityModel<Book> entityModel = EntityModel.of(updatedBook,
                    linkTo(methodOn(BookController.class).getBook(id)).withSelfRel(),
                    linkTo(methodOn(BookController.class).deleteBook(id)).withRel("delete"),
                    linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all"));
            return ResponseEntity.ok().body(entityModel);
        } catch (BookNotFoundEx e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id) {
        try {
            boolean deleted = dataRepo.deleteBook(id);
            if (!deleted) {
                // If no deletion occurs (possibly redundant as the not found exception will trigger first)
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (BookNotFoundEx e) {
            // Directly return a simple message instead of using the Problem detail
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book with ID=" + id + " does not exist");
        }
    }


    @PatchMapping("/{id}/borrow")
    public ResponseEntity<?> borrowBook(@PathVariable int id) {
        Book book = dataRepo.getBook(id);
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.BORROWED);
            dataRepo.updateBook(book);
            return ResponseEntity.ok(EntityModel.of(book,
                    linkTo(methodOn(BookController.class).getBook(id)).withSelfRel(),
                    linkTo(methodOn(BookController.class).returnBook(id)).withRel("return"),
                    linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all")));
        } else {
            throw new ConflictEx("You cannot borrow a book unless they are AVAILABLE.");
        }
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable int id) {
        Book book = dataRepo.getBook(id);
        if (book.getStatus() == BookStatus.BORROWED) {
            book.setStatus(BookStatus.AVAILABLE);
            dataRepo.updateBook(book);
            return ResponseEntity.ok(EntityModel.of(book,
                    linkTo(methodOn(BookController.class).getBook(id)).withSelfRel(),
                    linkTo(methodOn(BookController.class).borrowBook(id)).withRel("borrow"),
                    linkTo(methodOn(BookController.class).deactivateBook(id)).withRel("deactivate"), // Ensure this link is conditional
                    linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all")));
        } else {
            throw new ConflictEx("You cannot return a book unless they are BORROWED.");
        }
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateBook(@PathVariable int id) {
        Book book = dataRepo.getBook(id);
        if (book.getStatus() == BookStatus.NOT_AVAILABLE) {
            book.setStatus(BookStatus.AVAILABLE);
            dataRepo.updateBook(book); // Ensure this updates the person correctly
            return ResponseEntity.ok(EntityModel.of(book,
                    linkTo(methodOn(BookController.class).getBook(id)).withSelfRel(),
                    linkTo(methodOn(BookController.class).borrowBook(id)).withRel("borrow"),
                    linkTo(methodOn(BookController.class).deactivateBook(id)).withRel("deactivate"), // Ensure this link is conditional
                    linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all")));
        } else {
            throw new ConflictEx("You cannot activate a book who is not in NOT_AVAILABLE status.");
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateBook(@PathVariable int id) {
        Book book = dataRepo.getBook(id);
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.NOT_AVAILABLE);
            dataRepo.updateBook(book);
            return ResponseEntity.ok(EntityModel.of(book,
                    linkTo(methodOn(BookController.class).getBook(id)).withSelfRel(),
                    linkTo(methodOn(BookController.class).activateBook(id)).withRel("activate"),
                    linkTo(methodOn(BookController.class).getAllBooks()).withRel("list all")));
        } else {
            throw new ConflictEx("You cannot deactivate a book who is not in AVAILABLE status.");
        }
    }


}
