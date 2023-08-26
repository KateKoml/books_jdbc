package org.example.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ConnectionSetting;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.BookRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BookRepositoryImpl implements BookRepository {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String YEAR = "year";
    private static final String AUTHOR_ID = "author_id";

    private final ConnectionSetting connectionSetting;

    public BookRepositoryImpl(ConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    public List<Book> findAll() {
        final String findAllQuery = "SELECT * FROM books ORDER BY id DESC";

        List<Book> books = new ArrayList<>();

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(findAllQuery)
        ) {
            while (rs.next()) {
                books.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }

        return books;
    }

    public Book findById(Long id) {
        final String findByIdQuery = "SELECT * FROM books WHERE id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(findByIdQuery)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return parseResultSet(rs);
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Optional<Book> findOptionalById(Long id) {
        final String findByIdQuery = "SELECT * FROM books WHERE id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(findByIdQuery)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return Optional.of(parseResultSet(rs));
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Book create(Book book) {
        final String createQuery = "INSERT INTO books (name, year, author_id) VALUES (?, ?, ?)";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getName());
            statement.setInt(2, book.getYear());
            statement.setLong(3, book.getAuthorId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating book failed, no ID obtained.");
                    }
                }
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return book;
    }

    @Override
    public Book update(Book book) {
        final String updateQuery = "UPDATE books SET name = ?, year = ?, author_id = ? WHERE id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, book.getName());
            statement.setInt(2, book.getYear());
            statement.setLong(3, book.getAuthorId());
            statement.setLong(4, book.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Updating book failed, no rows affected.");
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return book;
    }

    @Override
    public boolean delete(Long id) {
        final String deleteQuery = "DELETE FROM books WHERE id = ?";
        final String linkQuery = "DELETE FROM l_books_genres WHERE book_id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement beforeStatement = connection.prepareStatement(linkQuery);
             PreparedStatement mainStatement = connection.prepareStatement(deleteQuery)
        ) {
            beforeStatement.setLong(1, id);
            beforeStatement.executeUpdate();
            mainStatement.setLong(1, id);
            mainStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public boolean setBookGenre(Long bookId, Integer genreId) {
        final String query = "INSERT INTO l_books_genres (book_id, genre_id) VALUES (?, ?)";
        boolean result = false;

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (!isBookGenreExists(bookId, genreId)) {
                statement.setLong(1, bookId);
                statement.setInt(2, genreId);
                statement.executeUpdate();
                result = true;
            }

        } catch (SQLException e) {
            result = false;
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
        return result;
    }

    private boolean isBookGenreExists(Long bookId, Integer genreId) {
        final String query = "SELECT * FROM l_books_genres WHERE book_id = ? AND genre_id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, bookId);
            statement.setInt(2, genreId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
            return false;
        }
    }

    public List<Genre> getGenresOfBook(Long bookId) {
        final String findQuery = "SELECT g.id, g.type FROM genres g " +
                "JOIN l_books_genres bg ON bg.genre_id = g.id " +
                "JOIN books b ON bg.book_id = b.id " +
                "WHERE b.id = ?";
        List<Genre> genres = new ArrayList<>();

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(findQuery)) {
            statement.setLong(1, bookId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setType(rs.getString("type"));
                    genres.add(genre);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
        return genres;
    }

    private Book parseResultSet(ResultSet rs) {
        Book book;

        try {
            book = new Book();
            book.setId(rs.getLong(ID));
            book.setName(rs.getString(NAME));
            book.setYear(rs.getInt(YEAR));
            book.setAuthorId(rs.getLong(AUTHOR_ID));
            List<Genre> genres = getGenresOfBook(book.getId());
            book.setGenres(genres);
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return book;
    }
}
