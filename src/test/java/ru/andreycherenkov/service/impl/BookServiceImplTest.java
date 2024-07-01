package ru.andreycherenkov.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.repository.impl.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void testSave() {
        // Given
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setIsbn("ISBN123");
        book.setPublicationYear(2023);
        book.setGenreId(1L);
        when(bookRepository.save(book)).thenReturn(book); // Задаем поведение мока для метода save

        // When
        Book savedBook = bookService.save(book);

        // Then
        assertEquals(book, savedBook); // Проверяем, что сохранение прошло успешно
        verify(bookRepository, times(1)).save(book); // Проверяем, что метод save был вызван один раз
    }

    @Test
    void testFindById_ExistingBook() {
        // Given
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setIsbn("ISBN123");
        book.setPublicationYear(2023);
        book.setGenreId(1L);
        when(bookRepository.findById(1L)).thenReturn(book); // Задаем поведение мока для findById

        // When
        Book foundBook = bookService.findById(1L);

        // Then
        assertEquals(book, foundBook); // Проверяем, что найденная книга совпадает с ожидаемой
        verify(bookRepository, times(1)).findById(1L); // Проверяем, что findById был вызван один раз
    }

    @Test
    void testFindById_NonExistingBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(null); // Задаем поведение мока для findById, чтобы вернуть Optional.empty()

        // When
        Book foundBook = bookService.findById(1L);

        // Then
        assertNull(foundBook); // Проверяем, что найденная книга равна null
        verify(bookRepository, times(1)).findById(1L); // Проверяем, что findById был вызван один раз
    }

    @Test
    void testDeleteById() {
        // Given
        when(bookRepository.deleteById(1L)).thenReturn(true); // Задаем поведение мока для deleteById

        // When
        boolean deleted = bookService.deleteById(1L);

        // Then
        assertTrue(deleted); // Проверяем, что удаление прошло успешно
        verify(bookRepository, times(1)).deleteById(1L); // Проверяем, что deleteById был вызван один раз
    }

    @Test
    void testFindAll() {
        // Given
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setIsbn("ISBN123");
        book.setPublicationYear(2023);
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book Title2");
        book2.setIsbn("ISBN123");
        book2.setPublicationYear(2022);
        List<Book> books = List.of(book, book2);
        when(bookRepository.findAll()).thenReturn(books); // Задаем поведение мока для findAll

        // When
        List<Book> allBooks = bookService.findAll();

        // Then
        assertEquals(books, allBooks); // Проверяем, что найденные книги совпадают с ожидаемыми
        verify(bookRepository, times(1)).findAll(); // Проверяем, что findAll был вызван один раз
    }
}