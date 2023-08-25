package org.example.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ConnectionSetting;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AuthorRepositoryImpl implements AuthorRepository {
    private static final String ID = "id";
    private static final String FULL_NAME = "full_name";
    private static final String YEAR_OF_BIRTH = "year_of_birth";

    private final ConnectionSetting connectionSetting;

    public AuthorRepositoryImpl(ConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    @Override
    public Author findById(Long id) {
        final String findByIdQuery = "SELECT * FROM authors WHERE id = ?";

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
    public Optional<Author> findOptionalById(Long id) {
        final String findByIdQuery = "SELECT * FROM authors WHERE id = ?";

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
    public List<Author> findAll() {
        final String findAllQuery = "SELECT * FROM authors ORDER BY id DESC";

        List<Author> authors = new ArrayList<>();

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(findAllQuery)
        ) {
            while (rs.next()) {
                authors.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }

        return authors;
    }

    @Override
    public Author create(Author author) {
        final String createQuery = "INSERT INTO authors (full_name, year_of_birth) VALUES (?, ?)";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, author.getFullName());
            statement.setInt(2, author.getYearOfBirth());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        author.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating author failed, no ID obtained.");
                    }
                }
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return author;
    }

    @Override
    public Author update(Author author) {
        final String updateQuery = "UPDATE authors SET full_name = ?, year_of_birth = ? WHERE id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, author.getFullName());
            statement.setInt(2, author.getYearOfBirth());
            statement.setLong(3, author.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Updating author failed, no rows affected.");
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return author;
    }

    @Override
    public boolean delete(Long id) {
        final String deleteQuery = "DELETE FROM authors WHERE id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    public List<Book> getAuthorsBooks(Long authorId) {
        final String findQuery = "SELECT b.id, b.name, b.year FROM books b " +
                "JOIN authors a ON b.author_id = a.id " +
                "WHERE a.id = ?";
        List<Book> books = new ArrayList<>();

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(findQuery)) {
            statement.setLong(1, authorId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getLong("id"));
                    book.setName(rs.getString("name"));
                    book.setYear(rs.getInt("year"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
        return books;
    }

    private Author parseResultSet(ResultSet rs) {
        Author author;

        try {
            author = new Author();
            author.setId(rs.getLong(ID));
            author.setFullName(rs.getString(FULL_NAME));
            author.setYearOfBirth(rs.getInt(YEAR_OF_BIRTH));
            List<Book> books = getAuthorsBooks(author.getId());
            author.setBooks(books);
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return author;
    }
}
