package ru.andreycherenkov.repository.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreycherenkov.model.Author;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorRepositoryTest {

    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
            .withDatabaseName("test")
            .withInitScript("db-migration.sql")
            .withUsername("test")
            .withPassword("test");

    private final AuthorRepository authorRepository = new AuthorRepository(
            mysqlContainer.getJdbcUrl(),
            mysqlContainer.getUsername(),
            mysqlContainer.getPassword()
    );

    @Test
    @Order(1)
    void findAllReturns5Authors() {
        assertEquals(5, authorRepository.findAll().size());
    }

    @Test
    @Order(2)
    void afterSaveAuthorFindAllReturn6Genres() {
        Author author = new Author();
        author.setFirstName("Bob");
        author.setLastName("Bab");
        authorRepository.save(author);
        assertEquals(6, authorRepository.findAll().size());
    }

    @Test
    @Order(3)
    void createNewAuthor() {
        Author author = new Author();
        author.setFirstName("TestFirstName");
        author.setLastName("TestLastName");
        Author savedAuthor = authorRepository.save(author);
        assertEquals(7, authorRepository.findAll().size());
        assertEquals("TestFirstName", author.getFirstName());
        assertEquals("TestLastName", author.getLastName());
    }

    @Test
    @Order(4)
    void findAuthorById1() {
        Author author = authorRepository.findById(1L);
        assertEquals("Джордж", author.getFirstName());
        assertEquals("Мартин", author.getLastName());
    }

    @Test
    @Order(5)
    void afterDeleteFindAllReturn6Authors() {
        boolean bool = authorRepository.deleteById(1L);
        assertTrue(bool);
        assertEquals(6, authorRepository.findAll().size());
    }

    @Test
    @Order(6)
    void whenUpdateAuthorThenIdDidntChangeAndFindAllReturn6Authors() {
        Author author = authorRepository.findById(2L);
        author.setFirstName("ChangeFirstName");
        author.setLastName("ChangeLastName");
        authorRepository.save(author);
        author = authorRepository.findById(2L);
        assertEquals(2L, author.getId());
        assertEquals("ChangeFirstName", author.getFirstName());
        assertEquals("ChangeLastName", author.getLastName());
        assertEquals(6 ,authorRepository.findAll().size());
    }

    @Test
    @Order(7)
    void whenFindById8ThenAuthorIdNull() {
        Author author = authorRepository.findById(8L);
        assertNull(author.getId());
    }

    @Test
    @Order(8)
    void WhenIdIsNegativeThenFindByIdReturnNullId() {
        Author author = authorRepository.findById(-1L);
        assertNull(author.getId());
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
