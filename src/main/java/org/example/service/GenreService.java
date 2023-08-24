package org.example.service;

import org.example.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Genre findById(Integer id);

    Optional<Genre> findOptionalById(Integer id);

    List<Genre> findAll();

    Genre create(Genre genre);

    Genre update(Genre genre);

    boolean delete(Integer id);
}
