package repository;

import org.example.config.ConnectionSetting;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.repository.GenreRepository;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.repository.impl.BookRepositoryImpl;
import org.example.repository.impl.GenreRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

class BookRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testjdbc")
            .withUsername("postgres")
            .withPassword("password")
            .withInitScript("migration/create_tables.sql");

    BookRepository bookRepository;
    AuthorRepository authorRepository;
    GenreRepository genreRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        ConnectionSetting connectionProvider = new ConnectionSetting();
        connectionProvider.setDatabaseUrl(postgres.getJdbcUrl());
        bookRepository = new BookRepositoryImpl(connectionProvider);
        authorRepository = new AuthorRepositoryImpl(connectionProvider);
        genreRepository = new GenreRepositoryImpl(connectionProvider);
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), "postgres", "password")) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM l_books_genres");
            statement.executeUpdate("DELETE FROM books");
            statement.executeUpdate("DELETE FROM authors");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createBookTest() {
        Author author = authorRepository.create(new Author("Jane Austen", 1775));
        bookRepository.create(new Book("Pride and Prejudice", 1813, author.getId()));
        bookRepository.create(new Book("Persuasion", 1816, author.getId()));

        List<Book> books = bookRepository.findAll();

        Assertions.assertEquals(2, books.size());
    }

    @Test
    void findByIdBookTest() {
        Author author = authorRepository.create(new Author("Jane Austen", 1775));
        Book newBook = bookRepository.create(new Book("Pride and Prejudice", 1813, author.getId()));

        Book book = bookRepository.findById(newBook.getId());

        Assertions.assertNotNull(book);
        Assertions.assertEquals(newBook.getName(), book.getName());
    }

    @Test
    void findOptionalByIdBookTest() {
        Author author = authorRepository.create(new Author("Jane Austen", 1775));
        Book newBook = bookRepository.create(new Book("Pride and Prejudice", 1813, author.getId()));

        Optional<Book> book = bookRepository.findOptionalById(newBook.getId());

        Assertions.assertNotNull(book);
        Assertions.assertTrue(book.isPresent());
    }

    @Test
    void updateBookTest() {
        Author author = authorRepository.create(new Author("Joanne Rowling", 1965));
        Book newBook = bookRepository.create(new Book("Harry Potter And The Cursed Child", 2016, author.getId()));

        newBook = bookRepository.update(new Book(newBook.getId(), "Harry Potter and the Philosopher's Stone", 1997, author.getId()));

        Assertions.assertEquals(1997, newBook.getYear());
    }

    @Test
    void findAllBooksTest() {
        Author author = authorRepository.create(new Author("Jane Austen", 1775));
        bookRepository.create(new Book("Pride and Prejudice", 1813, author.getId()));
        bookRepository.create(new Book("Persuasion", 1816, author.getId()));
        bookRepository.create(new Book("Emma", 1815, author.getId()));

        List<Book> books = bookRepository.findAll();

        Assertions.assertEquals(3, books.size());
    }

    @Test
    void deleteBookTest() {
        Author author = authorRepository.create(new Author("Jane Austen", 1775));
        Book book1 = bookRepository.create(new Book("Pride and Prejudice", 1813, author.getId()));
        Book book2 = bookRepository.create(new Book("Persuasion", 1816, author.getId()));
        Book book3 = bookRepository.create(new Book("Emma", 1815, author.getId()));

        boolean deleted = bookRepository.delete(book2.getId());
        List<Book> books = bookRepository.findAll();

        Assertions.assertEquals(2, books.size());
        Assertions.assertTrue(deleted);
    }

    @Test
    void setBookGenreTest() {
        Author author = authorRepository.create(new Author("Jane Austen", 1775));
        Genre genre = genreRepository.create(new Genre("romance"));
        Book book = bookRepository.create(new Book("Pride and Prejudice", 1813, author.getId()));

        boolean result = bookRepository.setBookGenre(book.getId(), genre.getId());

        Assertions.assertTrue(result);
    }
}
