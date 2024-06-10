package ru.andreycherenkov.repository.impl;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.ContainerConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.repository.AuthorCRUDRepository;
import ru.andreycherenkov.repository.mapper.AuthorMapper;
import ru.andreycherenkov.repository.mapper.AuthorResultSetMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorRepository implements AuthorCRUDRepository {

    private ConnectionManager connectionManager;
    private AuthorResultSetMapper authorResultSetMapper;

    public AuthorRepository() {
        this.connectionManager = new MySQLConnectionManager();
        this.authorResultSetMapper = new AuthorMapper();
    }

    public AuthorRepository(String url, String username, String password) {
        this.connectionManager = new ContainerConnectionManager(url, username, password);
        this.authorResultSetMapper = new AuthorMapper();
    }

    /*String sql = "SELECT a.author_id, a.first_name, a.last_name, " +
                    "b.book_id, b.title, b.isbn, b.publication_year " +
                    "FROM authors a " +
                    "JOIN author_book ab ON a.author_id = ab.author_id " +
                    "JOIN books b ON ab.book_id = b.book_id " +
                    "WHERE a.author_id = ?";*/
    @Override
    public Author findById(Long id) {
        if (id == null) {
            return null;
        }
        try(Connection connection = connectionManager.getConnection()) {
            String sql = "SELECT a.author_id, a.first_name, a.last_name, " +
                    "b.book_id, b.title, b.isbn, b.publication_year " +
                    "FROM authors a " +
                    "JOIN author_book ab ON a.author_id = ab.author_id " +
                    "JOIN books b ON ab.book_id = b.book_id " +
                    "WHERE a.author_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Author author = authorResultSetMapper.map(resultSet);

            if(author == null) {
                author = new Author();
                sql = "SELECT * " +
                        "FROM authors " +
                        "WHERE author_id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    author.setFirstName("first_name");
                    author.setLastName("last_name");
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

            String deleteAuthorBookSql = "DELETE FROM author_book WHERE author_id = ?";
            PreparedStatement deleteAuthorBookStatement = connection.prepareStatement(deleteAuthorBookSql);
            deleteAuthorBookStatement.setLong(1, id);
            deleteAuthorBookStatement.executeUpdate();

            String deleteAuthorSql = "DELETE FROM authors WHERE id = ?";
            PreparedStatement deleteAuthorStatement = connection.prepareStatement(deleteAuthorSql);
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
            String sql = "SELECT *" +
                        "FROM authors";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Author> authors = new ArrayList<>();
            while (resultSet.next()) {
                authors.add(authorResultSetMapper.map(resultSet));
            }
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
            if (findById(author.getId()) == null) {
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
            String sql = "INSERT INTO authors (first_name, last_name) VALUES (?, ?)";
            connection.setAutoCommit(false); // Отключаем автокоммит
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys(); // Получаем сгенерированный ID
            if (generatedKeys.next()) {
                author.setId(generatedKeys.getLong(1)); // Устанавливаем ID автору
            }

            if (!author.getBooks().isEmpty()) {
                sql = "INSERT INTO author_book (book_id, author_id) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
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
            String sql = "UPDATE author SET first_name = ?, last_name = ? WHERE author_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
