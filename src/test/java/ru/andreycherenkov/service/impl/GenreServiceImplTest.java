package ru.andreycherenkov.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.repository.impl.GenreRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    void testSave() {
        Genre genre = new Genre();
        when(genreRepository.save(genre)).thenReturn(genre);

        Genre savedGenre = genreService.save(genre);

        assertNotNull(savedGenre);
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Genre genre = new Genre();
        when(genreRepository.findById(id)).thenReturn(genre);

        Genre foundGenre = genreService.findById(id);

        assertEquals(genre, foundGenre);
        verify(genreRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;
        when(genreRepository.deleteById(id)).thenReturn(true);

        boolean deleted = genreService.deleteById(id);

        assertTrue(deleted);
        verify(genreRepository, times(1)).deleteById(id);
    }

    @Test
    void testFindAll() {
        List<Genre> genres = Arrays.asList(new Genre(), new Genre());
        when(genreRepository.findAll()).thenReturn(genres);

        List<Genre> foundGenres = genreService.findAll();

        assertEquals(genres, foundGenres);
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() {
        when(genreRepository.findAll()).thenReturn(Collections.emptyList());

        List<Genre> foundGenres = genreService.findAll();

        assertTrue(foundGenres.isEmpty());
        verify(genreRepository, times(1)).findAll();
    }
}