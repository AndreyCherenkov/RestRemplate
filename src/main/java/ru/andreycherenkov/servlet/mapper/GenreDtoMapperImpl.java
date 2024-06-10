package ru.andreycherenkov.servlet.mapper;

import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.servlet.dto.IncomingGenreDto;
import ru.andreycherenkov.servlet.dto.OutgoingGenreDto;

import java.util.stream.Collectors;

public class GenreDtoMapperImpl implements GenreDtoMapper {

    @Override
    public Genre map(IncomingGenreDto incomingGenreDto) {
        Genre genre = new Genre();
        genre.setName(incomingGenreDto.name());
        return genre;
    }

    @Override
    public OutgoingGenreDto map(Genre genre) {
        return new OutgoingGenreDto(
                genre.getId(),
                genre.getName(),
                genre.getBooks()
                        .stream()
                        .map(book -> book.getId())
                        .collect(Collectors.toList())
        );
    }
}
