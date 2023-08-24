package org.example.service;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book findById(Long id);

    Optional<Book> findOptionalById(Long id);

    List<Book> findAll();

    Book create(Book book);

    Book update(Book book);

    boolean delete(Long id);

    void setBookGenre(Long bookId, Integer genreId);
}
