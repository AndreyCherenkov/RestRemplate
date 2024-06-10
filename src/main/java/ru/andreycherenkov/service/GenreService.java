package ru.andreycherenkov.service;


import ru.andreycherenkov.model.Genre;

import java.util.List;

public interface GenreService {

    Genre save(Genre genre);

    Genre findById(Long id);

    boolean deleteById(Long id);

    List<Genre> findAll();
}
