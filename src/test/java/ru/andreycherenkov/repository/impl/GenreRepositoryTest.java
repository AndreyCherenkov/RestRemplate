package ru.andreycherenkov.repository.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreycherenkov.db.ConnectionManager;
import ru.andreycherenkov.db.impl.ContainerConnectionManager;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.mapper.BookMapper;
import ru.andreycherenkov.repository.mapper.BookResultMapper;
import ru.andreycherenkov.repository.mapper.GenreMapper;
import ru.andreycherenkov.repository.mapper.GenreResultSetMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreRepositoryTest {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
            .withDatabaseName("test")
            .withInitScript("db-migration.sql")
            .withUsername("test")
            .withPassword("test");

    private final ConnectionManager connectionManager = new ContainerConnectionManager(
            mysqlContainer.getJdbcUrl(),
            mysqlContainer.getUsername(),
            mysqlContainer.getPassword()
    );

    private final GenreResultSetMapper genreResultSetMapper = new GenreMapper();
    private final BookResultMapper bookResultMapper = new BookMapper();
    private final BookRepository bookRepository = new BookRepository(connectionManager, bookResultMapper);

    private final GenreRepository genreRepository = new GenreRepository(
            connectionManager,
            genreResultSetMapper,
            bookRepository
    );

    @Test
    @Order(1)
    void findAllReturnFiveGenres() {
        List<Genre> genres = genreRepository.findAll();
        assertEquals(5, genres.size());
    }

    @Test
    @Order(2)
    void afterSaveGenreFindAllReturn6Genres() {
        Genre genre = new Genre();
        genre.setName("test");
        genreRepository.save(genre);

        assertEquals(6, genreRepository.findAll().size());
    }

    @Test
    @Order(3)
    void createNewGenre() {
        Genre genre = new Genre();
        genre.setName("newgenre");
        Genre savedGenre = genreRepository.save(genre);

        assertEquals("newgenre", savedGenre.getName());
    }

    @Test
    @Order(4)
    void findGenreById1() {
        Genre genre = genreRepository.findById(1L);
        assertEquals("фантастика", genre.getName());
    }

    @Test
    @Order(5)
    void afterDeleteMethodFindAllReturn6Genres() {
        boolean bool = genreRepository.deleteById(1L);

        assertTrue(bool);
        assertEquals(6, genreRepository.findAll().size());
    }

    @Test
    @Order(6)
    void whenUpdateGenreThenIdDidntChangeAndFindAllReturn6Genres() {
        Genre genre = genreRepository.findById(6L);
        genre.setName("changedname");
        genreRepository.save(genre);
        genre = genreRepository.findById(6L);

        assertEquals(6, genreRepository.findAll().size());
        assertEquals(6L, genre.getId());
        assertEquals("changedname", genre.getName());
    }

    @Test
    @Order(7)
    void whenFindById8ThenGenreIdAndNameAreNull() {
        Genre genre = genreRepository.findById(8L);
        assertNull(genre.getId());
    }

    @Test
    @Order(8)
    void whenIdIsNegativeThenGenreIsNull() {
        Genre genre = genreRepository.findById(-1L);
        assertNull(genre.getId());
    }

    @AfterAll
    public static void tearDown() {
        try (Connection connection = DriverManager.getConnection(mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword())) {
            Statement stmt = connection.createStatement();
            stmt.execute("DELETE FROM author_book");
            stmt.execute("DELETE FROM books");
            stmt.execute("DELETE FROM authors");
            stmt.execute("DELETE FROM genres");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

