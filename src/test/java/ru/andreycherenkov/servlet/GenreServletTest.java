package ru.andreycherenkov.servlet;


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
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.service.GenreService;
import ru.andreycherenkov.servlet.dto.IncomingGenreDto;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServletTest {

    private GenreService genreService = mock(GenreService.class);
    @InjectMocks
    private GenreServlet genreServlet;
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);
    private BufferedReader reader = mock(BufferedReader.class);

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(response).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(genreService);
    }

    @Test
    void doGetByid() throws IOException{
        when(request.getParameter("id")).thenReturn("1");
        genreServlet.doGet(request, response);
        verify(genreService).findById(Mockito.anyLong());
    }

    @Test
    void doGetAll() throws IOException {
        when(request.getParameter("id")).thenReturn("all");

        genreServlet.doGet(request, response);

        verify(genreService).findAll();
    }

    @Test
    void doGetNotFoundStatus() throws IOException {
        when(request.getParameter("id")).thenReturn(Integer.MAX_VALUE + "");

        genreServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetInternalServerErrorStatus() throws IOException {
        when(request.getParameter("id")).thenReturn("error");

        genreServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void doPost() throws IOException {
        String jsonRequest = "{\"name\":\"test\"}";

        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        genreServlet.doPost(request, response);

        ArgumentCaptor<Genre> argumentCaptor = ArgumentCaptor.forClass(Genre.class);
        verify(genreService).save(argumentCaptor.capture());

        IncomingGenreDto result = new IncomingGenreDto(argumentCaptor.getValue().getName());
        assertEquals("test", result.name());
    }

    @Test
    void doDelete() throws IOException {
        when(request.getParameter("id")).thenReturn("1");
        genreServlet.doDelete(request, response);

        verify(genreService).deleteById(Mockito.anyLong());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteInternalServerErrorStatus() throws IOException {
        when(request.getParameter("id")).thenReturn("error");
        genreServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void doPut() throws IOException {
        String jsonRequest = "{\"name\":\"test\"}";

        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(jsonRequest, null);
        genreServlet.doPut(request, response);

        ArgumentCaptor<Genre> argumentCaptor = ArgumentCaptor.forClass(Genre.class);
        verify(genreService).save(argumentCaptor.capture());

        IncomingGenreDto result = new IncomingGenreDto(argumentCaptor.getValue().getName());
        assertEquals("test", result.name());

    }
}
