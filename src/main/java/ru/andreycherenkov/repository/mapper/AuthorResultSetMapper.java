package ru.andreycherenkov.repository.mapper;

import ru.andreycherenkov.model.Author;

import java.sql.ResultSet;

public interface AuthorResultSetMapper {
    Author map(ResultSet resultSet);
}
