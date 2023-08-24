package org.example.service.impl;

import org.example.model.Genre;
import org.example.repository.GenreRepository;
import org.example.service.GenreService;

import java.util.List;
import java.util.Optional;

public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre findById(Integer id) {
        return genreRepository.findById(id);
    }

    @Override
    public Optional<Genre> findOptionalById(Integer id) {
        return genreRepository.findOptionalById(id);
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre create(Genre genre) {
        return genreRepository.create(genre);
    }

    @Override
    public Genre update(Genre genre) {
        return genreRepository.update(genre);
    }

    @Override
    public boolean delete(Integer id) {
        return genreRepository.delete(id);
    }
}
