package ru.andreycherenkov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.impl.GenreRepository;

import java.util.List;

@Testcontainers
class GenreRepositoryTest {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
            .withDatabaseName("test")
            .withInitScript("db-migration.sql")
            .withUsername("test")
            .withPassword("test");

    private final GenreRepository genreRepository = new GenreRepository(mysqlContainer.getJdbcUrl(),
            mysqlContainer.getUsername(),
            mysqlContainer.getPassword());

    @Test
    void findAllReturnFiveGenres() {
        List<Genre> genres = genreRepository.findAll();
        assertEquals(5, genres.size());
    }

    @Test
    void afterSaveGenreFindAllReturn6Genres() {
        Genre genre = new Genre();
        genre.setName("test");
        genreRepository.save(genre);
        assertEquals(6, genreRepository.findAll().size());
    }

}

