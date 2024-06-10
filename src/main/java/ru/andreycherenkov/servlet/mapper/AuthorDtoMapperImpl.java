package ru.andreycherenkov.servlet.mapper;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.repository.BookCRUDRepository;
import ru.andreycherenkov.repository.impl.BookRepository;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.service.impl.BookServiceImpl;
import ru.andreycherenkov.servlet.dto.IncomingAuthorDto;
import ru.andreycherenkov.servlet.dto.OutgoingAuthorDto;

import java.util.stream.Collectors;

public class AuthorDtoMapperImpl implements AuthorDtoMapper{

    private final BookService bookService = new BookServiceImpl();

    @Override
    public Author map(IncomingAuthorDto incomingAuthorDto) {
        Author author = new Author();
        author.setFirstName(incomingAuthorDto.firstName());
        author.setLastName(incomingAuthorDto.lastName());
        author.setBooks(incomingAuthorDto.bookIds()
                .stream()
                .map(id -> bookService.findById(id))
                .collect(Collectors.toList()));
        return author;
    }

    @Override
    public OutgoingAuthorDto map(Author author) {
        return new OutgoingAuthorDto(author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBooks()
                        .stream()
                        .map(book -> book.getId())
                        .collect(Collectors.toList()));
    }
}
