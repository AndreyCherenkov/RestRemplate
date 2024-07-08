package ru.andreycherenkov.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.repository.impl.AuthorRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void testSave() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");

        when(authorRepository.save(author)).thenReturn(author);

        Author savedAuthor = authorService.save(author);

        assertEquals(author, savedAuthor);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testFindById_ExistingAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");

        when(authorRepository.findById(1L)).thenReturn(author);

        Author foundAuthor = authorService.findById(1L);

        assertEquals(author, foundAuthor);
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NonExistingAuthor() {
        when(authorRepository.findById(1L)).thenReturn(null);

        Author foundAuthor = authorService.findById(1L);

        assertNull(foundAuthor);
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        when(authorRepository.deleteById(1L)).thenReturn(true);

        boolean deleted = authorService.deleteById(1L);

        assertTrue(deleted);
        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAll() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        Author author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Jane");
        author2.setLastName("Doe");
        List<Author> authors = List.of(author, author2);

        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> allAuthors = authorService.findAll();

        assertEquals(authors, allAuthors);
        verify(authorRepository, times(1)).findAll();
    }
}
