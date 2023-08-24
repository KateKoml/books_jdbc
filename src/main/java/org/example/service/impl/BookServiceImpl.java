package org.example.service.impl;

import org.example.model.Book;
import org.example.repository.BookRepository;
import org.example.service.BookService;

import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Optional<Book> findOptionalById(Long id) {
        return bookRepository.findOptionalById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book create(Book book) {
        return bookRepository.create(book);
    }

    @Override
    public Book update(Book book) {
        return bookRepository.update(book);
    }

    @Override
    public boolean delete(Long id) {
        return bookRepository.delete(id);
    }
}
