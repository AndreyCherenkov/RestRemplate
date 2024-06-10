package ru.andreycherenkov.service;

import ru.andreycherenkov.model.Book;

import java.util.List;

public interface BookService {

    Book save(Book book);

    Book findById(Long id);

    boolean deleteById(Long id);

    List<Book> findAll();
}
