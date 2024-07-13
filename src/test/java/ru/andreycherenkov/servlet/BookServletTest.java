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
import ru.andreycherenkov.servlet.dto.IncomingBookDto;
import ru.andreycherenkov.servlet.mapper.BookDtoMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServletTest {

    private BookService bookService = mock(BookService.class);
    private BookDtoMapper bookDtoMapper = mock(BookDtoMapper.class);

    @InjectMocks
    private BookServlet bookServlet;

    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final BufferedReader reader = mock(BufferedReader.class);

    @BeforeEach
    void setUp() throws IOException {
        doReturn(new PrintWriter(Writer.nullWriter())).when(response).getWriter();
    }

    @AfterEach
    void tearDown() {
        reset(bookService);
        reset(bookDtoMapper);
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
        String title = "testTitle";
        String isbn = "978-0-618-05326-75";
        int publicationYear = 1954;
        Long genreId = 11L;
        String jsonRequest = "{\n" +
                "  \"title\": \"" + title + "\",\n" +
                "  \"isbn\": \"" + isbn + "\",\n" +
                "  \"publicationYear\": " + publicationYear + ",\n" +
                "  \"genreId\": " + genreId + ",\n" +
                "  \"authorIds\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    3\n" +
                "  ]\n" +
                "}\n";

        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublicationYear(publicationYear);
        book.setGenreId(genreId);
        when(bookDtoMapper.map(new IncomingBookDto(title, isbn, publicationYear, genreId, List.of(1L, 2L, 3L)))).thenReturn(book);
        bookServlet.doPost(request, response);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService).save(argumentCaptor.capture());

        assertEquals(title, argumentCaptor.getValue().getTitle());
        assertEquals(isbn, argumentCaptor.getValue().getIsbn());
        assertEquals(publicationYear, argumentCaptor.getValue().getPublicationYear());
        assertEquals(genreId, argumentCaptor.getValue().getGenreId());
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
        String jsonRequest = "{\n" +
                "  \"title\": \"" + title + "\",\n" +
                "  \"isbn\": \"" + isbn + "\",\n" +
                "  \"publicationYear\": " + publicationYear + ",\n" +
                "  \"genreId\": " + genreId + ",\n" +
                "  \"authorIds\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    3\n" +
                "  ]\n" +
                "}\n";
        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublicationYear(publicationYear);
        book.setGenreId(genreId);
        when(bookDtoMapper.map(new IncomingBookDto(title, isbn, publicationYear, genreId, List.of(1L, 2L, 3L)))).thenReturn(book);
        bookServlet.doPost(request, response);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService).save(argumentCaptor.capture());

        assertEquals(title, argumentCaptor.getValue().getTitle());
        assertEquals(isbn, argumentCaptor.getValue().getIsbn());
        assertEquals(publicationYear, argumentCaptor.getValue().getPublicationYear());
        assertEquals(genreId, argumentCaptor.getValue().getGenreId());
    }
}
