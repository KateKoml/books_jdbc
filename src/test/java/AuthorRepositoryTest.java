import org.example.config.ConnectionSetting;
import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.repository.impl.AuthorRepositoryImpl;
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

class AuthorRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testjdbc")
            .withUsername("postgres")
            .withPassword("password")
            .withInitScript("migration/create_tables.sql");

    AuthorRepository authorRepository;

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
        authorRepository = new AuthorRepositoryImpl(connectionProvider);
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), "postgres", "password")) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM authors");
            // выполните DELETE-запросы для всех таблиц базы данных
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createAuthor() {
        authorRepository.create(new Author("Charlotte Bronte", 1816));
        authorRepository.create(new Author("Jane Austen", 1775));

        List<Author> authors = authorRepository.findAll();

        Assertions.assertEquals(2, authors.size());
    }

    @Test
    void findByIdAuthor() {
        Author newAuthor = authorRepository.create(new Author("Charlotte Bronte", 1816));
        Author author = authorRepository.findById(newAuthor.getId());

        Assertions.assertEquals(newAuthor.getFullName(), author.getFullName());
    }

    @Test
    void findOptionalByIdAuthor() {
        Author newAuthor = authorRepository.create(new Author("Charlotte Bronte", 1816));
        Optional<Author> author = authorRepository.findOptionalById(newAuthor.getId());

        Assertions.assertTrue(author.isPresent());
    }

    @Test
    void updateAuthor() {
        Author author = authorRepository.create(new Author("Joanne Rowling", 1965));;
        author = authorRepository.update(new Author(author.getId(), "Joanne Rowling", 1970));

        Assertions.assertEquals(1970, author.getYearOfBirth());
    }

    @Test
    void findAllAuthor() {
        authorRepository.create(new Author("Charlotte Bronte", 1816));
        authorRepository.create(new Author("Jane Austen", 1775));
        List<Author> authors = authorRepository.findAll();

        Assertions.assertEquals(2, authors.size());
    }

    @Test
    void deleteAuthor() {
        Author author1 = authorRepository.create(new Author("Charlotte Bronte", 1816));
        Author author2 = authorRepository.create(new Author("Jane Austen", 1775));

        boolean deleted = authorRepository.delete(author1.getId());
        List<Author> authors = authorRepository.findAll();

        Assertions.assertEquals(1, authors.size());
        Assertions.assertTrue(deleted);

    }

}
