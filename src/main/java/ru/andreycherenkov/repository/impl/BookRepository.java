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

import java.sql.*;
import java.util.*;

public class BookRepository implements CRUDRepository<Book, Long> {

    private ConnectionManager connectionManager;
    private BookResultMapper bookResultMapper;

    public BookRepository() {
        this.connectionManager = new MySQLConnectionManager();
        this.bookResultMapper = new BookMapper();
    }

    public BookRepository(String url, String username, String password) {
        this.connectionManager = new ContainerConnectionManager(url, username, password);
        this.bookResultMapper = new BookMapper();
    }

    /*String sql = "SELECT b.book_id, b.title, b.isbn, b.publication_year, " +
                    "g.genre_id, g.name, " +
                    "a.author_id, a.first_name, a.last_name " +
                    "FROM books b " +
                    "JOIN genres g ON b.genre_id = g.genre_id " +
                    "JOIN author_book ba ON b.book_id = ba.book_id " +
                    "JOIN authors a ON ba.author_id = a.author_id " +
                    "WHERE b.book_id = ?";*/

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
            String sql = "SELECT b.book_id, b.title, b.isbn, b.publication_year, " +
                    "g.genre_id, g.name, " +
                    "a.author_id, a.first_name, a.last_name " +
                    "FROM books b " +
                    "JOIN genres g ON b.genre_id = g.genre_id " +
                    "JOIN author_book ba ON b.book_id = ba.book_id " +
                    "JOIN authors a ON ba.author_id = a.author_id " +
                    "WHERE b.book_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Book book = bookResultMapper.map(resultSet);

            if (book == null) {
                book = new Book();
                sql = "SELECT * " +
                        "FROM books " +
                        "WHERE book_id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    book.setId(resultSet.getLong("book_id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setIsbn(resultSet.getString("isbn"));
                    book.setGenreId(resultSet.getLong("genre_id"));
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

            String deleteAuthorBookSql = "DELETE FROM author_book WHERE book_id = ?";
            PreparedStatement deleteAuthorBookStatement = connection.prepareStatement(deleteAuthorBookSql);
            deleteAuthorBookStatement.setLong(1, id);
            deleteAuthorBookStatement.executeUpdate();

            String deleteBookSql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement deleteBookStatement = connection.prepareStatement(deleteBookSql);
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
            String sql = "SELECT b.book_id, b.title, b.isbn, b.publication_year, " +
                    "g.genre_id, g.name, " +
                    "a.author_id, a.first_name, a.last_name " +
                    "FROM books b " +
                    "LEFT JOIN genres g ON b.genre_id = g.genre_id " +
                    "LEFT JOIN author_book ba ON b.book_id = ba.book_id " +
                    "LEFT JOIN authors a ON ba.author_id = a.author_id ";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            Map<Long, Book> bookMap = new HashMap<>();

            while (resultSet.next()) {
                long bookId = resultSet.getLong("book_id");
                Book book = bookMap.getOrDefault(bookId, new Book());

                book.setId(bookId);
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setPublicationYear(resultSet.getInt("publication_year"));

                // Обработка жанра
                long genreId = resultSet.getLong("genre_id");
                if (genreId != 0) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(resultSet.getString("name"));
                    book.setGenre(genre);
                }

                // Обработка авторов
                long authorId = resultSet.getLong("author_id");
                if (authorId != 0) {
                    Author author = new Author();
                    author.setId(authorId);
                    author.setFirstName(resultSet.getString("first_name"));
                    author.setLastName(resultSet.getString("last_name"));
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
            String sql = "INSERT INTO books (title, isbn, publication_year, genre_id) VALUES (?, ?, ?, ?)";
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setInt(3, book.getPublicationYear());
            preparedStatement.setLong(4, book.getGenreId());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                book.setId(generatedKeys.getLong(1));
            }

            if (!book.getAuthors().isEmpty()) {
                sql = "INSERT INTO author_book (book_id, author_id) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
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
            String sql = "UPDATE books SET title = ?, isbn = ?, publication_year = ?, genre_id = ? WHERE book_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
