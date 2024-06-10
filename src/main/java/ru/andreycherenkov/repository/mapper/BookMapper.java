package ru.andreycherenkov.repository.mapper;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements BookResultMapper{

    @Override
    public Book map(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getLong("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setPublicationYear(resultSet.getInt("publication_year"));
                book.setGenreId(resultSet.getLong("genre_id"));
                book.getGenre().setName(resultSet.getString("name"));
                do {
                    Author author = new Author();
                    author.setId(resultSet.getLong("author_id"));
                    author.setFirstName(resultSet.getString("first_name"));
                    author.setLastName(resultSet.getString("last_name"));
                    book.addAuthor(author);
                } while (resultSet.next());


                return book;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
