package ru.andreycherenkov.servlet.mapper;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.servlet.dto.IncomingBookDto;
import ru.andreycherenkov.servlet.dto.OutgoingBookDto;

public interface BookDtoMapper {

    Book map(IncomingBookDto incomingBookDto);

    OutgoingBookDto map(Book book);
}
