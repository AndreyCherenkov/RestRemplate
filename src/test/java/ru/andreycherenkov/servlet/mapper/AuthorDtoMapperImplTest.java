package ru.andreycherenkov.servlet.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.servlet.dto.IncomingAuthorDto;
import ru.andreycherenkov.servlet.dto.OutgoingAuthorDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorDtoMapperImplTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private AuthorDtoMapperImpl authorDtoMapper;

    @Test
    void testMapIncomingAuthorDto() {
        // Given
        IncomingAuthorDto incomingAuthorDto = new IncomingAuthorDto(
                "John",
                "Doe",
                List.of(1L, 2L)
        );
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setIsbn("123");
        book1.setPublicationYear(2022);
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setIsbn("132");
        book2.setPublicationYear(2021);

        when(bookService.findById(1L)).thenReturn(book1);
        when(bookService.findById(2L)).thenReturn(book2);

        Author author = authorDtoMapper.map(incomingAuthorDto);

        assertEquals("John", author.getFirstName());
        assertEquals("Doe", author.getLastName());
        assertEquals(List.of(book1, book2), author.getBooks());
    }

    @Test
    void testMapAuthor() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setIsbn("123");
        book1.setPublicationYear(2022);
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setIsbn("132");
        book2.setPublicationYear(2021);

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBooks(List.of(book1, book2));

        OutgoingAuthorDto outgoingAuthorDto = authorDtoMapper.map(author);

        assertEquals(1L, outgoingAuthorDto.id());
        assertEquals("John", outgoingAuthorDto.firstName());
        assertEquals("Doe", outgoingAuthorDto.lastName());
        assertEquals(List.of(1L, 2L), outgoingAuthorDto.bookIds());
    }
}
