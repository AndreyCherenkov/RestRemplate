package ru.andreycherenkov.repository.impl;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.ContainerConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.CRUDRepository;
import ru.andreycherenkov.repository.mapper.GenreMapper;
import ru.andreycherenkov.repository.mapper.GenreResultSetMapper;

import java.sql.*;
import java.util.*;

public class GenreRepository implements CRUDRepository<Genre, Long> {

    private final ConnectionManager connectionManager;
    private final GenreResultSetMapper genreResultSetMapper;
    private final BookRepository bookRepository;

    public GenreRepository() {
        this.connectionManager = new MySQLConnectionManager();
        this.genreResultSetMapper = new GenreMapper();
        this.bookRepository = new BookRepository();
    }

    public GenreRepository(String url, String username, String password) {
        this.connectionManager = new ContainerConnectionManager(url, username, password);
        this.genreResultSetMapper = new GenreMapper();
        this.bookRepository = new BookRepository(url, username, password);
    }

    @Override
    public Genre findById(Long id) {
        if (id == null || id <= 0) {
            Genre genre = new Genre();
            genre.setId(null);
            genre.setName(null);
            return genre;
        }
        try(Connection connection = connectionManager.getConnection()) {
            String sql = "SELECT g.genre_id, g.name, b.book_id " +
                    "FROM genres g " +
                    "JOIN books b ON g.genre_id = b.genre_id " +
                    "WHERE g.genre_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Genre genre = genreResultSetMapper.map(resultSet);

            //Обработка случая, когда нет книг с данным жанром
            if (genre == null) {
                genre = new Genre();
                sql = "SELECT * FROM genres WHERE genre_id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    genre.setId(resultSet.getLong("genre_id"));
                    genre.setName(resultSet.getString("name"));
                }
            }
            return genre;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);

            // Обновляем книги, относящиеся к этому жанру
            String sql = "SELECT book_id FROM books WHERE genre_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Book book;
            while (resultSet.next()) {
                book = bookRepository.findById(resultSet.getLong("book_id"));
                book.setGenreId(null);
                bookRepository.save(book);
            }

            sql = "DELETE FROM genres WHERE genre_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            connection.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Genre> findAll() {
        try (Connection connection = connectionManager.getConnection()) {
            String sql = "SELECT g.genre_id, g.name, b.book_id " +
                    "FROM genres g " +
                    "LEFT JOIN books b ON g.genre_id = b.genre_id";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Genre> genres = new ArrayList<>();
            Map<Long, Genre> genreMap = new HashMap<>();

            while (resultSet.next()) {
                long genreId = resultSet.getLong("genre_id");
                Genre genre = genreMap.getOrDefault(genreId, new Genre());
                genre.setId(genreId);
                genre.setName(resultSet.getString("name"));

                long bookId = resultSet.getLong("book_id");
                if (bookId != 0) {
                    Book book = new Book();
                    book.setId(bookId);
                    genre.addBook(book);
                }

                genreMap.put(genreId, genre);
            }

            genres.addAll(genreMap.values());
            return genres;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Genre save(Genre genre) {
        try (Connection connection = connectionManager.getConnection()) {
            if (findById(genre.getId()).getId() == null) {
                Long id = createGenre(genre);
                genre.setId(id);
            } else {
                Long id = updateGenre(genre);
                genre.setId(id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return genre;
    }

    private Long createGenre(Genre genre) {
        try (Connection connection = connectionManager.getConnection()){
            String sql = "INSERT INTO genres (name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, genre.getName());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1L;
    }

    private Long updateGenre(Genre genre) {
        try (Connection connection = connectionManager.getConnection()){
            String sql = "UPDATE genres SET name = ? WHERE genre_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, genre.getName());
            preparedStatement.setLong(2, genre.getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1L;
    }
}
