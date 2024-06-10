package ru.andreycherenkov.service;

import ru.andreycherenkov.model.Author;

import java.util.List;

public interface AuthorService {

    Author save(Author author);

    Author findById(Long id);

    boolean deleteById(Long id);

    List<Author> findAll();
}
