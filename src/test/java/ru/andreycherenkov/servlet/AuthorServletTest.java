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
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.service.AuthorService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServletTest {

    private AuthorService authorService = mock(AuthorService.class);

    @InjectMocks
    private AuthorServlet authorServlet;

    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final BufferedReader reader = mock(BufferedReader.class);

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(response).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authorService);
    }

    @Test
    void doGetById() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        authorServlet.doGet(request, response);
        verify(authorService).findById(Mockito.anyLong());
    }

    @Test
    void doGetAll() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("all");
        authorServlet.doGet(request, response);
        verify(authorService).findAll();
    }

    @Test
    void doGetNotFound() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn(Integer.MAX_VALUE + "");
        authorServlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetInternalServerError() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("error");
        authorServlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void doPost() throws IOException, ServletException {
        String firstname = "testName";
        String lastname = "testLastName";
        String jsonRequest = "{\"firstName\":\"" + firstname + "\"" + ",\"lastName\":\"" + lastname + "\"" + ",\"bookIds\":[1,2,3]}";

        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);

        authorServlet.doPost(request, response);
        ArgumentCaptor<Author> argumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorService).save(argumentCaptor.capture());

        assertEquals(firstname, argumentCaptor.getValue().getFirstName());
        assertEquals(lastname, argumentCaptor.getValue().getLastName());
        assertEquals(3, argumentCaptor.getValue().getBooks().size());
    }

    @Test
    void doDelete() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        authorServlet.doDelete(request, response);

        verify(authorService).deleteById(Mockito.anyLong());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
        void doDeleteInternalServerError() throws IOException, ServletException {
        when(request.getParameter("id")).thenReturn("error");

        authorServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void doPut() throws IOException, ServletException {
        String firstname = "testName";
        String lastname = "testLastName";
        String jsonRequest = "{\"firstName\":\"" + firstname + "\"" + ",\"lastName\":\"" + lastname + "\"" + ",\"bookIds\":[1,2,3]}";

        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        authorServlet.doPut(request, response);

        ArgumentCaptor<Author> argumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorService).save(argumentCaptor.capture());

        assertEquals(firstname, argumentCaptor.getValue().getFirstName());
        assertEquals(lastname, argumentCaptor.getValue().getLastName());
        assertEquals(3, argumentCaptor.getValue().getBooks().size());
    }
}
