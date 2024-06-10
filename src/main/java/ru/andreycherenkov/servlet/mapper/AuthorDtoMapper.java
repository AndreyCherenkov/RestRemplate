package ru.andreycherenkov.servlet.mapper;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.servlet.dto.IncomingAuthorDto;
import ru.andreycherenkov.servlet.dto.OutgoingAuthorDto;

public interface AuthorDtoMapper {

    Author map(IncomingAuthorDto incomingAuthorDto);

    OutgoingAuthorDto map(Author author);
}
