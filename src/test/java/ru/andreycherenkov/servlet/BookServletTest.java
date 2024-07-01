package ru.andreycherenkov.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.service.BookService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServletTest {

    private BookService bookService = mock(BookService.class);

    @InjectMocks
    private BookServlet bookServlet;

    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final BufferedReader reader = mock(BufferedReader.class);

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(response).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(bookService);
    }

    @Test
    void doGetById() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        bookServlet.doGet(request, response);
        verify(bookService).findById(Mockito.anyLong());
    }

    @Test
    void doGetAll() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("all");
        bookServlet.doGet(request, response);
        verify(bookService).findAll();
    }

    @Test
    void doGetNotFound() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn(Integer.MAX_VALUE + "");
        bookServlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetInternalServerError() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("error");
        bookServlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void doPost() throws IOException, ServletException {
        String title = "test title";
        String isbn = "978-0-618-05326-7";
        int publicationYear = 1954;
        Long genreId = 1L;
        String jsonRequest = "{\"title\":\"" + title + "\",\"isbn\":\"" + isbn + "\",\"publicationYear\":" + publicationYear + ",\"genreId\":" + genreId + ",\"authorIds\":[1,2,3]}";
        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        bookServlet.doPost(request, response);
        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService).save(argumentCaptor.capture());
        assertEquals(title, argumentCaptor.getValue().getTitle());
        assertEquals(isbn, argumentCaptor.getValue().getIsbn());
        assertEquals(publicationYear, argumentCaptor.getValue().getPublicationYear());
        assertEquals(genreId, argumentCaptor.getValue().getGenreId());
        assertEquals(3, argumentCaptor.getValue().getAuthors().size());
    }

    @Test
    void doDelete() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        bookServlet.doDelete(request, response);
        verify(bookService).deleteById(Mockito.anyLong());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteInternalServerError() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("error");
        bookServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void doPut() throws IOException, ServletException {
        String title = "test title";
        String isbn = "978-0-618-05326-7";
        int publicationYear = 1954;
        Long genreId = 1L;
        String jsonRequest = "{\"title\":\"" + title + "\",\"isbn\":\"" + isbn + "\",\"publicationYear\":" + publicationYear + ",\"genreId\":" + genreId + ",\"authorIds\":[1,2,3]}";
        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        bookServlet.doPost(request, response);
        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService).save(argumentCaptor.capture());
        assertEquals(title, argumentCaptor.getValue().getTitle());
        assertEquals(isbn, argumentCaptor.getValue().getIsbn());
        assertEquals(publicationYear, argumentCaptor.getValue().getPublicationYear());
        assertEquals(genreId, argumentCaptor.getValue().getGenreId());
        assertEquals(3, argumentCaptor.getValue().getAuthors().size());
    }
}
