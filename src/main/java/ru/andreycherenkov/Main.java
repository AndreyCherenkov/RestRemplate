package ru.andreycherenkov;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.impl.AuthorRepository;
import ru.andreycherenkov.repository.impl.BookRepository;
import ru.andreycherenkov.repository.impl.GenreRepository;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.service.GenreService;
import ru.andreycherenkov.service.impl.AuthorServiceImpl;
import ru.andreycherenkov.service.impl.BookServiceImpl;
import ru.andreycherenkov.service.impl.GenreServiceImpl;
import ru.andreycherenkov.servlet.GenreServlet;
import ru.andreycherenkov.servlet.dto.*;
import ru.andreycherenkov.servlet.mapper.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        /*
        GenreService genreService = new GenreServiceImpl();
        GenreDtoMapper genreDtoMapper = new GenreDtoMapperImpl();

        AuthorService authorService = new AuthorServiceImpl();
        AuthorDtoMapper authorDtoMapper = new AuthorDtoMapperImpl();

        BookService bookService = new BookServiceImpl();
        BookDtoMapper bookDtoMapper = new BookDtoMapperImpl();

        Long genre_id = 1L;
        Genre genre = genreService.findById(genre_id);
        OutgoingGenreDto outgoingGenreDto = genreDtoMapper.map(genre);
        System.out.println(outgoingGenreDto);

        Long author_id = 1L;
        Author author = authorService.findById(author_id);
        OutgoingAuthorDto outgoingAuthorDto = authorDtoMapper.map(author);
        System.out.println(outgoingAuthorDto);

        Long book_id = 2L;
        Book book = bookService.findById(book_id);
        OutgoingBookDto outgoingBookDto = bookDtoMapper.map(book);
        System.out.println(outgoingBookDto);
        System.out.println(book);

        Book book1 = bookDtoMapper.map(new IncomingBookDto("Страдания",
                "913-1341",
                2010,
                1L,
                List.of(1L, 2L)));
        Book savedBook = bookService.save(book1);
        OutgoingBookDto mapBook = bookDtoMapper.map(savedBook);
        System.out.println(mapBook);
        */
        System.out.println(new GenreRepository().findAll().size());
    }
}