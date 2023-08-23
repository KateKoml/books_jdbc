package org.example.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ConnectionSetting;
import org.example.model.Book;
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
        final String findAllQuery = "select * from books order by id desc";

        List<Book> result = new ArrayList<>();

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(findAllQuery)
        ) {
            while (rs.next()) {
                result.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
        return result;
    }

    public Book findById(Long id) {
        final String findByIdQuery = "select * from books where id = ?";

        connectionSetting.registerDriver();
        List<Book> list = new ArrayList<>();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findByIdQuery)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Book finedBook = new Book();
        try {
            finedBook = list.get(0);
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            throw new RuntimeException("There is no such user id");
        }
        return finedBook;
    }

    @Override
    public Optional<Book> findOptionalById(Long id) {
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM books WHERE id = ?"
             )) {
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
             PreparedStatement MainStatement = connection.prepareStatement(deleteQuery)
        ) {
            beforeStatement.setLong(1, id);
            beforeStatement.executeUpdate();
            MainStatement.setLong(1, id);
            MainStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    private Book parseResultSet(ResultSet rs) {
        Book book;

        try {
            book = new Book();
            book.setId(rs.getLong(ID));
            book.setName(rs.getString(NAME));
            book.setYear(rs.getInt(YEAR));
            book.setAuthorId(rs.getLong(AUTHOR_ID));
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return book;
    }
}
