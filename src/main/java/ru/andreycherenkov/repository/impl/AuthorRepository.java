package ru.andreycherenkov.repository.impl;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.ContainerConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.repository.CRUDRepository;
import ru.andreycherenkov.repository.mapper.AuthorMapper;
import ru.andreycherenkov.repository.mapper.AuthorResultSetMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorRepository implements CRUDRepository<Author, Long> {

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
        if (id == null || id <= 0) {
            Author author = new Author();
            author.setId(null);
            author.setFirstName(null);
            author.setLastName(null);
            return author;
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

            String deleteAuthorSql = "DELETE FROM authors WHERE author_id = ?";
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
            // Используем LEFT JOIN, чтобы получить всех авторов, даже если у них нет книг
            String sql = "SELECT DISTINCT a.author_id, a.first_name, a.last_name, b.book_id, b.title, b.isbn, b.publication_year " +
                    "FROM authors a " +
                    "LEFT JOIN author_book ab ON a.author_id = ab.author_id " +
                    "LEFT JOIN books b ON ab.book_id = b.book_id";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Author> authors = new ArrayList<>();
            Map<Long, Author> authorMap = new HashMap<>();

            while (resultSet.next()) {
                long authorId = resultSet.getLong("author_id");
                Author author = authorMap.getOrDefault(authorId, new Author());

                author.setId(authorId);
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));

                // Проверяем, есть ли у автора книги
                if (resultSet.getObject("book_id") != null) {
                    // Добавление информации о книге
                    long bookId = resultSet.getLong("book_id");
                    String title = resultSet.getString("title");
                    String isbn = resultSet.getString("isbn");
                    int publicationYear = resultSet.getInt("publication_year");

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
            String sql = "UPDATE authors SET first_name = ?, last_name = ? WHERE author_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setLong(3, author.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
