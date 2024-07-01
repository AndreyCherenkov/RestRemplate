package ru.andreycherenkov.service.impl;

import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.impl.GenreRepository;
import ru.andreycherenkov.service.GenreService;

import java.util.List;

public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Genre findById(Long id) {
        return genreRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        return genreRepository.deleteById(id);
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }
}
