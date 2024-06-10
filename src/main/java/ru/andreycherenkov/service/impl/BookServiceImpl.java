package ru.andreycherenkov.service.impl;

import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.repository.impl.BookRepository;
import ru.andreycherenkov.service.BookService;

import java.util.List;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository = new BookRepository();

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        return bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
