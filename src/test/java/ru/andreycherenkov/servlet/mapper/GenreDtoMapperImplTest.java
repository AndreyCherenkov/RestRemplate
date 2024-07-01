package ru.andreycherenkov.servlet.mapper;

import org.junit.jupiter.api.Test;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.servlet.dto.IncomingGenreDto;
import ru.andreycherenkov.servlet.dto.OutgoingGenreDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenreDtoMapperImplTest {

    private final GenreDtoMapperImpl genreDtoMapper = new GenreDtoMapperImpl();

    @Test
    void testMapIncomingGenreDto() {
        // given
        IncomingGenreDto incomingGenreDto = new IncomingGenreDto("Test Genre");

        // when
        Genre genre = genreDtoMapper.map(incomingGenreDto);

        // then
        assertNotNull(genre);
        assertEquals("Test Genre", genre.getName());
    }

    @Test
    void testMapGenre() {
        // given
        Book book1 = new Book();
        book1.setId(1L);
        Book book2 = new Book();
        book2.setId(2L);
        List<Book> books = Arrays.asList(book1, book2);

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Test Genre");
        genre.setBooks(books);

        List<Long> expectedBookIds = Arrays.asList(1L, 2L);

        // when
        OutgoingGenreDto outgoingGenreDto = genreDtoMapper.map(genre);

        // then
        assertNotNull(outgoingGenreDto);
        assertEquals(1L, outgoingGenreDto.id());
        assertEquals("Test Genre", outgoingGenreDto.name());
        assertEquals(expectedBookIds, outgoingGenreDto.bookIds());
    }
}
