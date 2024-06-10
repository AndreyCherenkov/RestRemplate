package ru.andreycherenkov.repository.mapper;

import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements GenreResultSetMapper{

    @Override
    public Genre map(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                Genre genre = new Genre();
                genre.setId(resultSet.getLong("genre_id"));
                genre.setName(resultSet.getString("name"));
                do {
                    Book book = new Book();
                    book.setId(resultSet.getLong("book_id"));
                    genre.addBook(book);
                } while (resultSet.next());
                return genre;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
