package ru.andreycherenkov.servlet;


import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.service.GenreService;
import ru.andreycherenkov.servlet.dto.OutgoingGenreDto;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GenreServletTest {

    @Test
    void doGet_success() throws IOException {
        // 1. Подготовка моков
        GenreService genreService = mock(GenreService.class);
        GenreDtoMapper genreDtoMapper = mock(GenreDtoMapper.class);
        GenreServlet genreServlet = new GenreServlet();

        // 2. Создание тестовых данных
        Long genreId = 1L;
        String genreName = "ужасы";
        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName(genreName);
        OutgoingGenreDto outgoingGenreDto = new OutgoingGenreDto(genreId, genreName, List.of(2L, 7L, 11L));

        // 3. Ожидание поведения моков
        when(genreService.findById(genreId)).thenReturn(genre);
        when(genreDtoMapper.map(genre)).thenReturn(outgoingGenreDto);

        // 4. Создание запроса
        //MockHttpServletRequest request = new MockHttpServletRequest();
        //request.setParameter("id", genreId.toString());

        // 5. Создание ответа
        //MockHttpServletResponse response = new MockHttpServletResponse();

        // 6. Вызов doGet
        //genreServlet.doGet(request, response);

        // 7. Проверка ответа
        //assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        //assertEquals("application/json", response.getContentType());
        //assertEquals("UTF-8", response.getCharacterEncoding());

        // 8. Проверка содержимого ответа (с помощью StringWriter)
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        //response.getWriter().write(stringWriter);
        assertEquals(new Gson().toJson(outgoingGenreDto), stringWriter.toString());

        // 9. Проверка вызовов моков
        verify(genreService, times(1)).findById(genreId);
        verify(genreDtoMapper, times(1)).map(genre);
    }

}
