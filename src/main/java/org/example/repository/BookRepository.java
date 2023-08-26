package org.example.repository;

import org.example.model.Book;

public interface BookRepository extends CrudRepository<Long, Book> {
    boolean setBookGenre(Long bookId, Integer genreId);
}
