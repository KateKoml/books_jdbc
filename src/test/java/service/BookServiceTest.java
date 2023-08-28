package service;

import org.example.model.Book;
import org.example.repository.BookRepository;
import org.example.service.BookService;
import org.example.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;
    BookService bookService;

    @BeforeEach
    public void setUp() {
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    void testFindBookById() {
        Book book = new Book();
        book.setId(1L);
        book.setName("Big fish");
        book.setYear(1978);
        book.setAuthorId(1L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(book);

        Assertions.assertEquals(book, bookService.findById(1L));
    }

    @Test
    void testFindOptionalBookById() {
        Book book = new Book();
        book.setId(1L);
        book.setName("Big fish");
        book.setYear(1978);
        book.setAuthorId(1L);

        Mockito.when(bookRepository.findOptionalById(1L)).thenReturn(Optional.of(book));

        Assertions.assertEquals(Optional.of(book), bookService.findOptionalById(1L));
    }

    @Test
    void testFindAllBooks() {
        List<Book> books = new ArrayList<>();
        Book book1 = new Book();
        book1.setId(1L);
        book1.setName("Big fish");
        book1.setYear(1978);
        book1.setAuthorId(1L);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setName("Big cat");
        book2.setYear(1980);
        book2.setAuthorId(1L);
        books.add(book1);
        books.add(book2);

        Mockito.when(bookRepository.findAll()).thenReturn(books);

        Assertions.assertEquals(books, bookService.findAll());
    }

    @Test
    void testCreateBook() {
        Book book = new Book();
        book.setId(1L);
        book.setName("Big fish");
        book.setYear(1978);
        book.setAuthorId(1L);

        Mockito.when(bookRepository.create(book)).thenReturn(book);

        Assertions.assertEquals(book, bookService.create(book));
    }

    @Test
    void testUpdateBook() {
        Book book = new Book();
        book.setId(1L);
        book.setName("Big fish");
        book.setYear(1978);
        book.setAuthorId(1L);

        Mockito.when(bookRepository.update(book)).thenReturn(book);

        Assertions.assertEquals(book, bookService.update(book));
    }

    @Test
    void testDeleteBook() {
        Mockito.when(bookRepository.delete(1L)).thenReturn(true);

        Assertions.assertTrue(bookService.delete(1L));
    }
}
