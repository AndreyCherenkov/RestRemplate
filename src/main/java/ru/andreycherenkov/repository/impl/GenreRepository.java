package ru.andreycherenkov.repository.impl;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.ContainerConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.CRUDRepository;
import ru.andreycherenkov.repository.mapper.GenreMapper;
import ru.andreycherenkov.repository.mapper.GenreResultSetMapper;
import ru.andreycherenkov.statements.BookStatement;
import ru.andreycherenkov.statements.GenreStatement;

import java.sql.*;
import java.util.*;

public class GenreRepository implements CRUDRepository<Genre, Long> {

    private final ConnectionManager connectionManager;
    private final GenreResultSetMapper genreResultSetMapper;
    private final BookRepository bookRepository;

    public GenreRepository(ConnectionManager connectionManager,
                           GenreResultSetMapper genreResultSetMapper,
                           BookRepository bookRepository) {
        this.connectionManager = connectionManager;
        this.genreResultSetMapper = genreResultSetMapper;
        this.bookRepository = bookRepository;
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

            PreparedStatement preparedStatement = connection.prepareStatement(GenreStatement.SELECT_JOIN_GENRE.getValue());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Genre genre = genreResultSetMapper.map(resultSet);

            //Обработка случая, когда нет книг с данным жанром
            if (genre == null) {
                genre = new Genre();
                preparedStatement = connection.prepareStatement(GenreStatement.SELECT_GENRE.getValue());
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    genre.setId(resultSet.getLong(GenreStatement.GENRE_ID.getValue()));
                    genre.setName(resultSet.getString(GenreStatement.NAME.getValue()));
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

            PreparedStatement preparedStatement = connection.prepareStatement(GenreStatement.SELECT_BOOK_ID_FROM_BOOKS.getValue());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Book book;
            while (resultSet.next()) {
                book = bookRepository.findById(resultSet.getLong(BookStatement.BOOK_ID.getValue()));
                book.setGenreId(null);
                bookRepository.save(book);
            }

            preparedStatement = connection.prepareStatement(GenreStatement.DELETE_BOOK.getValue());
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
            PreparedStatement preparedStatement = connection.prepareStatement(GenreStatement.FIND_ALL.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Genre> genres = new ArrayList<>();
            Map<Long, Genre> genreMap = new HashMap<>();

            while (resultSet.next()) {
                long genreId = resultSet.getLong(GenreStatement.GENRE_ID.getValue());
                Genre genre = genreMap.getOrDefault(genreId, new Genre());
                genre.setId(genreId);
                genre.setName(resultSet.getString(GenreStatement.NAME.getValue()));

                long bookId = resultSet.getLong(BookStatement.BOOK_ID.getValue());
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
            PreparedStatement preparedStatement = connection.prepareStatement(GenreStatement.INSERT_GENRE.getValue(), Statement.RETURN_GENERATED_KEYS);
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
            PreparedStatement preparedStatement = connection.prepareStatement(GenreStatement.UPDATE_GENRE.getValue(), Statement.RETURN_GENERATED_KEYS);
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
