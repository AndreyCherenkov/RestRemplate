package ru.andreycherenkov.repository.impl;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.ContainerConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.CRUDRepository;
import ru.andreycherenkov.repository.mapper.BookMapper;
import ru.andreycherenkov.repository.mapper.BookResultMapper;
import ru.andreycherenkov.statements.AuthorStatement;
import ru.andreycherenkov.statements.BookStatement;
import ru.andreycherenkov.statements.GenreStatement;

import java.sql.*;
import java.util.*;

public class BookRepository implements CRUDRepository<Book, Long> {

    private ConnectionManager connectionManager;
    private BookResultMapper bookResultMapper;

    public BookRepository(ConnectionManager connectionManager, BookResultMapper bookResultMapper) {
        this.connectionManager = connectionManager;
        this.bookResultMapper = bookResultMapper;
    }

    @Override
    public Book findById(Long id) {
        if (id == null || id <= 0) {
            Book book = new Book();
            book.setId(null);
            book.setAuthor(null);
            book.setTitle(null);
            book.setIsbn(null);
            return book;
        }
        try (Connection connection = connectionManager.getConnection()){

            PreparedStatement preparedStatement = connection.prepareStatement(BookStatement.SELECT_JOIN_BOOK.getValue());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Book book = bookResultMapper.map(resultSet);

            if (book == null) {
                book = new Book();

                preparedStatement = connection.prepareStatement(BookStatement.SELECT_BOOK.getValue());
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    book.setId(resultSet.getLong(BookStatement.BOOK_ID.getValue()));
                    book.setTitle(resultSet.getString(BookStatement.TITLE.getValue()));
                    book.setIsbn(resultSet.getString(BookStatement.ISBN.getValue()));
                    book.setGenreId(resultSet.getLong(GenreStatement.GENRE_ID.getValue()));
                }
            }
            return book;
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false); // Отключаем автокоммит

            PreparedStatement deleteAuthorBookStatement = connection.prepareStatement(BookStatement.DELETE_AUTHOR_BOOK.getValue());
            deleteAuthorBookStatement.setLong(1, id);
            deleteAuthorBookStatement.executeUpdate();

            PreparedStatement deleteBookStatement = connection.prepareStatement(BookStatement.DELETE_BOOK.getValue());
            deleteBookStatement.setLong(1, id);
            int rowsAffected = deleteBookStatement.executeUpdate();

            connection.commit(); // Совершаем коммит транзакции
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Book> findAll() {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(BookStatement.FIND_ALL.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            Map<Long, Book> bookMap = new HashMap<>();

            while (resultSet.next()) {
                long bookId = resultSet.getLong(BookStatement.BOOK_ID.getValue());
                Book book = bookMap.getOrDefault(bookId, new Book());

                book.setId(bookId);
                book.setTitle(resultSet.getString(BookStatement.TITLE.getValue()));
                book.setIsbn(resultSet.getString(BookStatement.ISBN.getValue()));
                book.setPublicationYear(resultSet.getInt(BookStatement.PUBLICATION_YEAR.getValue()));

                // Обработка жанра
                long genreId = resultSet.getLong(GenreStatement.GENRE_ID.getValue());
                if (genreId != 0) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(resultSet.getString(GenreStatement.NAME.getValue()));
                    book.setGenre(genre);
                }

                // Обработка авторов
                long authorId = resultSet.getLong(AuthorStatement.AUTHOR_ID.getValue());
                if (authorId != 0) {
                    Author author = new Author();
                    author.setId(authorId);
                    author.setFirstName(resultSet.getString(AuthorStatement.FIRST_NAME.getValue()));
                    author.setLastName(resultSet.getString(AuthorStatement.LAST_NAME.getValue()));
                    book.addAuthor(author);
                }

                bookMap.put(bookId, book);
            }

            books.addAll(bookMap.values());
            preparedStatement.close();
            return books;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Book save(Book book) {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);
            if (findById(book.getId()).getId() == null) {
                createBook(book);
            } else {
                updateBook(book);
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createBook(Book book) {
        try (Connection connection = connectionManager.getConnection()){
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(BookStatement.INSERT_INTO_BOOK.getValue(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setInt(3, book.getPublicationYear());
            if (book.getGenreId() != null) {
                preparedStatement.setLong(4, book.getGenreId());
            } else {
                preparedStatement.setNull(4, Types.NULL);
            }
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                book.setId(generatedKeys.getLong(1));
            }

            if (!book.getAuthors().isEmpty()) {
                preparedStatement = connection.prepareStatement(BookStatement.INSERT_INTO_AUTHOR_BOOK.getValue());
                for (Author author : book.getAuthors()) {
                    preparedStatement.setLong(1, book.getId());
                    preparedStatement.setLong(2, author.getId());
                    preparedStatement.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBook(Book book) {
        try (Connection connection = connectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(BookStatement.UPDATE_BOOK.getValue());
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setInt(3, book.getPublicationYear());
            if (book.getGenreId() == null) {
                preparedStatement.setNull(4, Types.NULL);
            } else {
                preparedStatement.setLong(4, book.getGenreId());
            }
            preparedStatement.setLong(5, book.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
