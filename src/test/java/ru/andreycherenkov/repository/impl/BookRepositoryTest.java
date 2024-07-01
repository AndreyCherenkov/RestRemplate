package ru.andreycherenkov.repository.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.model.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookRepositoryTest {

    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
            .withDatabaseName("test")
            .withInitScript("db-migration.sql")
            .withUsername("test")
            .withPassword("test");

    private final BookRepository bookRepository = new BookRepository(
            mysqlContainer.getJdbcUrl(),
            mysqlContainer.getUsername(),
            mysqlContainer.getPassword()
    );

    @Test
    @Order(1)
    void findAllReturns5Books() {
        assertEquals(5, bookRepository.findAll().size());
    }

    @Test
    @Order(2)
    void afterSaveBookFindAllReturns6Books() {
        Book book = new Book();
        book.setTitle("Test Title");
        book.setIsbn("123456789");
        book.setGenreId(1L);
        bookRepository.save(book);
        assertEquals(6, bookRepository.findAll().size());
    }

    @Test
    @Order(3)
    void createNewBook() {
        Book book = new Book();
        book.setTitle("Test Title2");
        book.setIsbn("1234567891");
        book.setGenreId(1L);
        Book savedBook = bookRepository.save(book);
        assertEquals("Test Title2", savedBook.getTitle());
    }

    @Test
    @Order(4)
    void findBookById1() {
        Book book = bookRepository.findById(1L);
        assertEquals("Игра престолов", book.getTitle());
    }

    @Test
    @Order(5)
    void afterDeleteFindAllReturns6Books() {
        boolean bool = bookRepository.deleteById(1L);
        assertTrue(bool);
        assertEquals(6, bookRepository.findAll().size());
    }

    @Test
    @Order(6)
    void whenUpdateGenreThenIdDidntChangeAndFindAllReturn6Books() {
        Book book = bookRepository.findById(6L);
        book.setTitle("Changed Title");
        bookRepository.save(book);
        book = bookRepository.findById(6L);
        assertEquals("Changed Title", book.getTitle());
        assertEquals(6, bookRepository.findAll().size());
        assertEquals(6L, book.getId());
    }

    @Test
    @Order(7)
    void whenFindById8ThenAllBookDataAreNull() {
        Book book = bookRepository.findById(8L);
        assertNull(book.getGenreId());
        assertNull(book.getTitle());
        assertNull(book.getIsbn());
    }

    @Test
    @Order(8)
    void whenIdIsNegativeThenBookDataAreNull() {
        Book book = bookRepository.findById(-1L);
        assertNull(book.getGenreId());
        assertNull(book.getTitle());
        assertNull(book.getIsbn());
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
