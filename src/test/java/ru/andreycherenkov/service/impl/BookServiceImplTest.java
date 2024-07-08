package ru.andreycherenkov.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.repository.impl.BookRepository;

import java.util.List;

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
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setIsbn("ISBN123");
        book.setPublicationYear(2023);
        book.setGenreId(1L);
        when(bookRepository.save(book)).thenReturn(book);

        Book savedBook = bookService.save(book);

        assertEquals(book, savedBook);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testFindById_ExistingBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setIsbn("ISBN123");
        book.setPublicationYear(2023);
        book.setGenreId(1L);

        when(bookRepository.findById(1L)).thenReturn(book);

        Book foundBook = bookService.findById(1L);

        assertEquals(book, foundBook);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NonExistingBook() {
        when(bookRepository.findById(1L)).thenReturn(null);

        Book foundBook = bookService.findById(1L);

        assertNull(foundBook);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        when(bookRepository.deleteById(1L)).thenReturn(true);

        boolean deleted = bookService.deleteById(1L);

        assertTrue(deleted);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAll() {
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

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> allBooks = bookService.findAll();

        // Then
        assertEquals(books, allBooks);
        verify(bookRepository, times(1)).findAll();
    }
}