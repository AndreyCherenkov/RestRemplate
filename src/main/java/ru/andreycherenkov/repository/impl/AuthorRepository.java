package ru.andreycherenkov.repository.impl;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.repository.CRUDRepository;
import ru.andreycherenkov.repository.mapper.AuthorMapper;
import ru.andreycherenkov.repository.mapper.AuthorResultSetMapper;
import ru.andreycherenkov.statements.AuthorStatement;
import ru.andreycherenkov.statements.BookStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorRepository implements CRUDRepository<Author, Long> {


    private final ConnectionManager connectionManager;
    private final AuthorResultSetMapper authorResultSetMapper;

    public AuthorRepository(ConnectionManager connectionManager, AuthorResultSetMapper authorResultSetMapper) {
        this.connectionManager = connectionManager;
        this.authorResultSetMapper = authorResultSetMapper;
    }

    @Override
    public Author findById(Long id) {
        if (id == null || id <= 0) {
            Author author = new Author();
            author.setId(null);
            author.setFirstName(null);
            author.setLastName(null);
            return author;
        }
        try(Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(AuthorStatement.SELECT_JOIN_AUTHOR.getValue());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Author author = authorResultSetMapper.map(resultSet);

            if(author == null) {
                author = new Author();
                preparedStatement = connection.prepareStatement(AuthorStatement.SELECT_AUTHOR.getValue());
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    author.setFirstName(AuthorStatement.FIRST_NAME.getValue());
                    author.setLastName(AuthorStatement.LAST_NAME.getValue());
                }
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement deleteAuthorBookStatement = connection.prepareStatement(AuthorStatement.DELETE_BOOK.getValue());
            deleteAuthorBookStatement.setLong(1, id);
            deleteAuthorBookStatement.executeUpdate();

            PreparedStatement deleteAuthorStatement = connection.prepareStatement(AuthorStatement.DELETE_AUTHOR.getValue());
            deleteAuthorStatement.setLong(1, id);
            int rowsAffected = deleteAuthorStatement.executeUpdate();

            connection.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Author> findAll() {
        try (Connection connection = connectionManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(AuthorStatement.FIND_ALL.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Author> authors = new ArrayList<>();
            Map<Long, Author> authorMap = new HashMap<>();

            while (resultSet.next()) {
                long authorId = resultSet.getLong(AuthorStatement.AUTHOR_ID.getValue());
                Author author = authorMap.getOrDefault(authorId, new Author());

                author.setId(authorId);
                author.setFirstName(resultSet.getString(AuthorStatement.FIRST_NAME.getValue()));
                author.setLastName(resultSet.getString(AuthorStatement.LAST_NAME.getValue()));

                if (resultSet.getObject(BookStatement.BOOK_ID.getValue()) != null) {
                    long bookId = resultSet.getLong(BookStatement.BOOK_ID.getValue());
                    String title = resultSet.getString(BookStatement.TITLE.getValue());
                    String isbn = resultSet.getString(BookStatement.ISBN.getValue());
                    int publicationYear = resultSet.getInt(BookStatement.PUBLICATION_YEAR.getValue());

                    Book book = new Book();
                    book.setId(bookId);
                    book.setTitle(title);
                    book.setIsbn(isbn);
                    book.setPublicationYear(publicationYear);

                    author.addBook(book);
                }

                authorMap.put(authorId, author);
            }

            authors.addAll(authorMap.values());
            preparedStatement.close();
            return authors;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Author save(Author author) {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);
            if (findById(author.getId()).getId() == null) {
                createAuthor(author);
            } else {
                updateAuthor(author);
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAuthor(Author author) {
        try (Connection connection = connectionManager.getConnection()){
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    AuthorStatement.INSERT_INTO_AUTHORS.getValue(),
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys(); // Получаем сгенерированный ID
            if (generatedKeys.next()) {
                author.setId(generatedKeys.getLong(1)); // Устанавливаем ID автору
            }

            if (!author.getBooks().isEmpty()) {
                preparedStatement = connection.prepareStatement(AuthorStatement.INSERT_INTO_AUTHOR_BOOK.getValue());
                for (Book book : author.getBooks()) {
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

    private void updateAuthor(Author author) {
        try (Connection connection = connectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(AuthorStatement.UPDATE_AUTHOR.getValue());
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setLong(3, author.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
