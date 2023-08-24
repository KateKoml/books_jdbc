package org.example.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ConnectionSetting;
import org.example.model.Genre;
import org.example.repository.GenreRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class GenreRepositoryImpl implements GenreRepository {
    private static final String ID = "id";
    private static final String TYPE = "type";

    private final ConnectionSetting connectionSetting;

    public GenreRepositoryImpl(ConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    @Override
    public Genre findById(Integer id) {
        final String findByIdQuery = "SELECT * FROM genres WHERE id = ?";

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
    public Optional<Genre> findOptionalById(Integer id) {
        final String findByIdQuery = "SELECT * FROM genres WHERE id = ?";

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
    public List<Genre> findAll() {
        final String findAllQuery = "SELECT * FROM genres ORDER BY id DESC";

        List<Genre> genres = new ArrayList<>();

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(findAllQuery)
        ) {
            while (rs.next()) {
                genres.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }

        return genres;
    }

    @Override
    public Genre create(Genre genre) {
        final String createQuery = "INSERT INTO genres (type) VALUES (?)";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, genre.getType());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        genre.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating genre failed, no ID obtained.");
                    }
                }
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        final String updateQuery = "UPDATE genres SET type = ? WHERE id = ?";

        connectionSetting.registerDriver();
        try (Connection connection = connectionSetting.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, genre.getType());
            statement.setLong(2, genre.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Updating author failed, no rows affected.");
            }

        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return genre;
    }

    @Override
    public boolean delete(Integer id) {
        final String deleteQuery = "DELETE FROM genres WHERE id = ?";
        final String linkQuery = "DELETE FROM l_books_genres WHERE genre_id = ?";

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

    private Genre parseResultSet(ResultSet rs) {
        Genre genre;

        try {
            genre = new Genre();
            genre.setId(rs.getInt(ID));
            genre.setType(rs.getString(TYPE));
        } catch (SQLException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }

        return genre;
    }
}
