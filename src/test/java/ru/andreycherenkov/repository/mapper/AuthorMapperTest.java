package ru.andreycherenkov.repository.mapper;

import org.junit.jupiter.api.Test;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorMapperTest {

    @Test
    void testMap() throws SQLException {
        // Подготовка тестовых данных
        Author expectedAuthor = new Author();
        expectedAuthor.setId(1L);
        expectedAuthor.setFirstName("John");
        expectedAuthor.setLastName("Doe");

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Test Book 1");
        book1.setIsbn("1234567890");
        book1.setPublicationYear(2022);
        expectedAuthor.addBook(book1);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Test Book 2");
        book2.setIsbn("0987654321");
        book2.setPublicationYear(2023);
        expectedAuthor.addBook(book2);

        // Создание мок-объекта ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false); // Два автора + false для выхода из цикла
        when(resultSet.getLong("author_id")).thenReturn(1L);
        when(resultSet.getString("first_name")).thenReturn("John");
        when(resultSet.getString("last_name")).thenReturn("Doe");

        when(resultSet.getLong("book_id")).thenReturn(1L, 2L);
        when(resultSet.getString("title")).thenReturn("Test Book 1", "Test Book 2");
        when(resultSet.getString("isbn")).thenReturn("1234567890", "0987654321");
        when(resultSet.getInt("publication_year")).thenReturn(2022, 2023);

        // Создание экземпляра AuthorMapper
        AuthorMapper authorMapper = new AuthorMapper();

        // Вызов метода map()
        Author actualAuthor = authorMapper.map(resultSet);

        // Проверка результата
        assertNotNull(actualAuthor);
        assertEquals(expectedAuthor.getId(), actualAuthor.getId());
        assertEquals(expectedAuthor.getFirstName(), actualAuthor.getFirstName());
        assertEquals(expectedAuthor.getLastName(), actualAuthor.getLastName());

        assertEquals(expectedAuthor.getBooks().size(), actualAuthor.getBooks().size());
        for (int i = 0; i < expectedAuthor.getBooks().size(); i++) {
            assertEquals(expectedAuthor.getBooks().get(i).getId(), actualAuthor.getBooks().get(i).getId());
            assertEquals(expectedAuthor.getBooks().get(i).getTitle(), actualAuthor.getBooks().get(i).getTitle());
            assertEquals(expectedAuthor.getBooks().get(i).getIsbn(), actualAuthor.getBooks().get(i).getIsbn());
            assertEquals(expectedAuthor.getBooks().get(i).getPublicationYear(), actualAuthor.getBooks().get(i).getPublicationYear());
        }
    }

    @Test
    void testMapWhenResultSetIsEmpty() throws SQLException {
        // Создание мок-объекта ResultSet, который вернет false при вызове next()
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        // Создание экземпляра AuthorMapper
        AuthorMapper authorMapper = new AuthorMapper();

        // Вызов метода map()
        Author author = authorMapper.map(resultSet);

        // Проверка, что результат равен null
        assertNull(author);
    }
}