package ru.andreycherenkov.repository.mapper;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorMapper implements AuthorResultSetMapper{

    @Override
    public Author map(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                Author author = new Author();
                author.setId(resultSet.getLong("author_id"));
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));

                do {
                    Book book = new Book();
                    book.setId(resultSet.getLong("book_id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setIsbn(resultSet.getString("isbn"));
                    book.setPublicationYear(resultSet.getInt("publication_year"));
                    author.addBook(book);
                } while (resultSet.next());
                return author;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
