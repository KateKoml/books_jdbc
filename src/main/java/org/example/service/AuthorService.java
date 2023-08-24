package org.example.service;

import org.example.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Author findById(Long id);

    Optional<Author> findOptionalById(Long id);

    List<Author> findAll();

    Author create(Author author);

    Author update(Author author);

    boolean delete(Long id);
}
