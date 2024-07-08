package ru.andreycherenkov.repository.mapper;

import org.junit.jupiter.api.Test;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookMapperTest {

    @Test
    void testMap() throws SQLException {
        Book expectedBook = new Book();
        expectedBook.setId(1L);
        expectedBook.setTitle("Test Title");
        expectedBook.setIsbn("1234567890");
        expectedBook.setPublicationYear(2023);
        expectedBook.setGenreId(1L);
        expectedBook.getGenre().setName("Test Genre");

        Author author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("John");
        author1.setLastName("Doe");
        expectedBook.addAuthor(author1);

        Author author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Jane");
        author2.setLastName("Doe");
        expectedBook.addAuthor(author2);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false); // Два автора + false для выхода из цикла
        when(resultSet.getLong("book_id")).thenReturn(1L);
        when(resultSet.getString("title")).thenReturn("Test Title");
        when(resultSet.getString("isbn")).thenReturn("1234567890");
        when(resultSet.getInt("publication_year")).thenReturn(2023);
        when(resultSet.getLong("genre_id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Test Genre");

        when(resultSet.getLong("author_id")).thenReturn(1L, 2L); // ID авторов
        when(resultSet.getString("first_name")).thenReturn("John", "Jane");
        when(resultSet.getString("last_name")).thenReturn("Doe", "Doe");

        BookMapper bookMapper = new BookMapper();
        Book actualBook = bookMapper.map(resultSet);

        assertNotNull(actualBook);
        assertEquals(expectedBook.getId(), actualBook.getId());
        assertEquals(expectedBook.getTitle(), actualBook.getTitle());
        assertEquals(expectedBook.getIsbn(), actualBook.getIsbn());
        assertEquals(expectedBook.getPublicationYear(), actualBook.getPublicationYear());
        assertEquals(expectedBook.getGenreId(), actualBook.getGenreId());
        assertEquals(expectedBook.getGenre().getName(), actualBook.getGenre().getName());

        assertEquals(expectedBook.getAuthors().size(), actualBook.getAuthors().size());
        for (int i = 0; i < expectedBook.getAuthors().size(); i++) {
            assertEquals(expectedBook.getAuthors().get(i).getId(), actualBook.getAuthors().get(i).getId());
            assertEquals(expectedBook.getAuthors().get(i).getFirstName(), actualBook.getAuthors().get(i).getFirstName());
            assertEquals(expectedBook.getAuthors().get(i).getLastName(), actualBook.getAuthors().get(i).getLastName());
        }
    }

    @Test
    void testMapWhenResultSetIsEmpty() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        BookMapper bookMapper = new BookMapper();

        Book book = bookMapper.map(resultSet);

        assertNull(book);
    }
}
