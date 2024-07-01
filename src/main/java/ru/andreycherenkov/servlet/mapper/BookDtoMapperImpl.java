package ru.andreycherenkov.servlet.mapper;

import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.servlet.dto.IncomingBookDto;
import ru.andreycherenkov.servlet.dto.OutgoingBookDto;

import java.util.stream.Collectors;

public class BookDtoMapperImpl implements BookDtoMapper {

    private final AuthorService authorService;

    public BookDtoMapperImpl(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public Book map(IncomingBookDto incomingBookDto) {
        Book book = new Book();
        book.setTitle(incomingBookDto.title());
        book.setIsbn(incomingBookDto.isbn());
        book.setPublicationYear(incomingBookDto.publicationYear());
        book.setGenreId(incomingBookDto.genreId());
        book.setAuthor(incomingBookDto.authorIds()
                .stream()
                .map(id -> authorService.findById(id))
                .collect(Collectors.toList()));
        return book;
    }

    @Override
    public OutgoingBookDto map(Book book) {
        return new OutgoingBookDto(book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getGenreId(),
                book.getAuthors()
                        .stream()
                        .map(author -> author.getId())
                        .collect(Collectors.toList())
                );
    }
}
