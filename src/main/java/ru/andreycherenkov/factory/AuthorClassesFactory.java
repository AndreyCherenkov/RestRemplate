package ru.andreycherenkov.factory;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.repository.impl.AuthorRepository;
import ru.andreycherenkov.repository.impl.BookRepository;
import ru.andreycherenkov.repository.mapper.AuthorMapper;
import ru.andreycherenkov.repository.mapper.AuthorResultSetMapper;
import ru.andreycherenkov.repository.mapper.BookMapper;
import ru.andreycherenkov.repository.mapper.BookResultMapper;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.service.impl.AuthorServiceImpl;
import ru.andreycherenkov.service.impl.BookServiceImpl;
import ru.andreycherenkov.servlet.mapper.AuthorDtoMapper;
import ru.andreycherenkov.servlet.mapper.AuthorDtoMapperImpl;

public class AuthorClassesFactory {

    private static final ConnectionManager connectionManager = new MySQLConnectionManager();
    private static final AuthorResultSetMapper authorResultSetMapper = new AuthorMapper();
    private static final BookResultMapper bookResultMapper = new BookMapper();

    private AuthorClassesFactory() {
    }

    public static AuthorService getDefaultAuthorService() {
        return new AuthorServiceImpl(new AuthorRepository(connectionManager, authorResultSetMapper));
    }

    public static AuthorDtoMapper getDefaultAuthorDtoMapper() {
        return new AuthorDtoMapperImpl(new BookServiceImpl(new BookRepository(connectionManager, bookResultMapper)));
    }

}
