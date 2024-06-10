package ru.andreycherenkov.servlet.mapper;

import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.servlet.dto.IncomingGenreDto;
import ru.andreycherenkov.servlet.dto.OutgoingGenreDto;

public interface GenreDtoMapper {

    Genre map(IncomingGenreDto incomingGenreDto);

    OutgoingGenreDto map(Genre genre);
}
