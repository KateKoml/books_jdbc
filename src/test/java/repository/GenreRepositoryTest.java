package repository;

import org.example.config.ConnectionSetting;
import org.example.model.Genre;
import org.example.repository.GenreRepository;
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

public class GenreRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testjdbc")
            .withUsername("postgres")
            .withPassword("password")
            .withInitScript("migration/create_tables.sql");

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
        genreRepository = new GenreRepositoryImpl(connectionProvider);
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), "postgres", "password")) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM genres");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createGenreTest() {
        genreRepository.create(new Genre("horror"));
        genreRepository.create(new Genre("romance"));

        List<Genre> genres = genreRepository.findAll();

        Assertions.assertEquals(2, genres.size());
    }

    @Test
    void findByIdGenreTest() {
        Genre newGenre = genreRepository.create(new Genre("fantasy"));

        Genre genre = genreRepository.findById(newGenre.getId());

        Assertions.assertNotNull(genre);
        Assertions.assertEquals(newGenre.getType(), genre.getType());
    }

    @Test
    void findOptionalByIdGenreTest() {
        Genre newGenre = genreRepository.create(new Genre("thriller"));

        Optional<Genre> genre = genreRepository.findOptionalById(newGenre.getId());

        Assertions.assertNotNull(genre);
        Assertions.assertTrue(genre.isPresent());
    }

    @Test
    void updateGenreTest() {
        Genre genre = genreRepository.create(new Genre("thriler"));

        genre = genreRepository.update(new Genre(genre.getId(), "thriller"));

        Assertions.assertEquals("thriller", genre.getType());
    }

    @Test
    void findAllGenreTest() {
        genreRepository.create(new Genre("horror"));
        genreRepository.create(new Genre("romance"));
        genreRepository.create(new Genre("thriller"));

        List<Genre> genres = genreRepository.findAll();

        Assertions.assertEquals(3, genres.size());
    }

    @Test
    void deleteGenreTest() {
        Genre genre = genreRepository.create(new Genre("horror"));
        Genre genre2 = genreRepository.create(new Genre("romance"));
        Genre genre3 = genreRepository.create(new Genre("thriller"));

        boolean deleted = genreRepository.delete(genre2.getId());
        List<Genre> genres = genreRepository.findAll();

        Assertions.assertEquals(2, genres.size());
        Assertions.assertTrue(deleted);
    }
}
