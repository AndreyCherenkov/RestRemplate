package ru.andreycherenkov.repository.mapper;

import org.junit.jupiter.api.Test;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreMapperTest {

    @Test
    void testMap() throws SQLException {
        // Подготовка тестовых данных
        Genre expectedGenre = new Genre();
        expectedGenre.setId(1L);
        expectedGenre.setName("Test Genre");

        Book book1 = new Book();
        book1.setId(1L);
        expectedGenre.addBook(book1);

        Book book2 = new Book();
        book2.setId(2L);
        expectedGenre.addBook(book2);

        // Создание мок-объекта ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false); // Два автора + false для выхода из цикла
        when(resultSet.getLong("genre_id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Test Genre");
        when(resultSet.getLong("book_id")).thenReturn(1L, 2L);

        // Создание экземпляра GenreMapper
        GenreMapper genreMapper = new GenreMapper();

        // Вызов метода map()
        Genre actualGenre = genreMapper.map(resultSet);

        // Проверка результата
        assertNotNull(actualGenre);
        assertEquals(expectedGenre.getId(), actualGenre.getId());
        assertEquals(expectedGenre.getName(), actualGenre.getName());

        assertEquals(expectedGenre.getBooks().size(), actualGenre.getBooks().size());
        for (int i = 0; i < expectedGenre.getBooks().size(); i++) {
            assertEquals(expectedGenre.getBooks().get(i).getId(), actualGenre.getBooks().get(i).getId());
        }
    }

    @Test
    void testMapWhenResultSetIsEmpty() throws SQLException {
        // Создание мок-объекта ResultSet, который вернет false при вызове next()
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        // Создание экземпляра GenreMapper
        GenreMapper genreMapper = new GenreMapper();

        // Вызов метода map()
        Genre genre = genreMapper.map(resultSet);

        // Проверка, что результат равен null
        assertNull(genre);
    }
}
