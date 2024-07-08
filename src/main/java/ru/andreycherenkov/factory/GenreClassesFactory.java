package ru.andreycherenkov.factory;

import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.MySQLConnectionManager;
import ru.andreycherenkov.repository.impl.BookRepository;
import ru.andreycherenkov.repository.impl.GenreRepository;
import ru.andreycherenkov.repository.mapper.BookMapper;
import ru.andreycherenkov.repository.mapper.BookResultMapper;
import ru.andreycherenkov.repository.mapper.GenreMapper;
import ru.andreycherenkov.repository.mapper.GenreResultSetMapper;
import ru.andreycherenkov.service.GenreService;
import ru.andreycherenkov.service.impl.GenreServiceImpl;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapper;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapperImpl;

public class GenreClassesFactory {

    private static final ConnectionManager connectionManager = new MySQLConnectionManager();
    private static final GenreResultSetMapper genreResultSetMapper = new GenreMapper();
    private static final BookResultMapper bookResultMapper = new BookMapper();

    private GenreClassesFactory() {
    }

    public static GenreService getDefaultGenreService() {
        return new GenreServiceImpl(new GenreRepository(connectionManager, genreResultSetMapper, new BookRepository(connectionManager, bookResultMapper)));
    }

    public static GenreDtoMapper getGenreDtoMapper() {
        return new GenreDtoMapperImpl();
    }

}
