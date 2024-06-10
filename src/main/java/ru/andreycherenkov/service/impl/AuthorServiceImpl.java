package ru.andreycherenkov.service.impl;

import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.repository.impl.AuthorRepository;
import ru.andreycherenkov.service.AuthorService;

import java.util.List;

public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository = new AuthorRepository();
    @Override
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Author findById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public List<Author> findAll() {
        return null;
    }
}
