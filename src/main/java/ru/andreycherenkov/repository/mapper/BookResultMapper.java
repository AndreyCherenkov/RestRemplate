package ru.andreycherenkov.repository.mapper;

import ru.andreycherenkov.model.Book;

import java.sql.ResultSet;

public interface BookResultMapper {

    Book map(ResultSet resultSet);
}
