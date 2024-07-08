package ru.andreycherenkov.factory;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.repository.impl.AuthorRepository;
import ru.andreycherenkov.repository.impl.BookRepository;
import ru.andreycherenkov.repository.mapper.AuthorMapper;
import ru.andreycherenkov.repository.mapper.AuthorResultSetMapper;
import ru.andreycherenkov.repository.mapper.BookMapper;
import ru.andreycherenkov.repository.mapper.BookResultMapper;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.service.impl.AuthorServiceImpl;
import ru.andreycherenkov.service.impl.BookServiceImpl;
import ru.andreycherenkov.servlet.mapper.BookDtoMapper;
import ru.andreycherenkov.servlet.mapper.BookDtoMapperImpl;

public class BookClassesFactory {

    private static ConnectionManager connectionManager = new MySQLConnectionManager();
    private static AuthorResultSetMapper authorResultSetMapper = new AuthorMapper();
    private static BookResultMapper bookResultMapper = new BookMapper();

    public static BookService getDefaultBookService() {
        return new BookServiceImpl(new BookRepository(connectionManager, bookResultMapper));
    }

    public static BookDtoMapper getDefaultBookDtoMapper() {
        return new BookDtoMapperImpl(new AuthorServiceImpl(new AuthorRepository(connectionManager, authorResultSetMapper)));
    }

}
