package ru.andreycherenkov.repository.mapper;

import ru.andreycherenkov.model.Genre;

import java.sql.ResultSet;

public interface GenreResultSetMapper {
    Genre map(ResultSet resultSet);
}
