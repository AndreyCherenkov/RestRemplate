package ru.andreycherenkov.servlet.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.servlet.dto.IncomingBookDto;
import ru.andreycherenkov.servlet.dto.OutgoingBookDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookDtoMapperImplTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private BookDtoMapperImpl bookDtoMapper;

    @Test
    void testMapIncomingBookDto() {
        // Given
        IncomingBookDto incomingBookDto = new IncomingBookDto(
                "Book Title",
                "1234567890",
                2023,
                1L,
                List.of(1L, 2L)
        );
        Author author1 = new Author();
        author1.setFirstName("Author 1");
        author1.setLastName("LastName 1");
        Author author2 = new Author();
        author2.setFirstName("Author 2");
        author2.setLastName("LastName 2");
        when(authorService.findById(1L)).thenReturn(author1);
        when(authorService.findById(2L)).thenReturn(author2);

        // When
        Book book = bookDtoMapper.map(incomingBookDto);

        // Then
        assertEquals("Book Title", book.getTitle());
        assertEquals("1234567890", book.getIsbn());
        assertEquals(2023, book.getPublicationYear());
        assertEquals(1L, book.getGenreId());
        assertEquals(List.of(author1, author2), book.getAuthors());
    }

    @Test
    void testMapBook() {
        // Given
        Author author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("Author 1");
        author1.setLastName("LastName 1");
        Author author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Author 2");
        author2.setLastName("LastName 2");
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setIsbn("1234567890");
        book.setPublicationYear(2023);
        book.setGenreId(1L);
        book.getAuthors().addAll(List.of(author1, author2));

        // When
        OutgoingBookDto outgoingBookDto = bookDtoMapper.map(book);

        // Then
        assertEquals(1L, outgoingBookDto.id());
        assertEquals("Book Title", outgoingBookDto.title());
        assertEquals("1234567890", outgoingBookDto.isbn());
        assertEquals(2023, outgoingBookDto.publicationYear());
        assertEquals(1L, outgoingBookDto.genre_id());
        assertEquals(List.of(1L, 2L), outgoingBookDto.authorIds());
    }
}
