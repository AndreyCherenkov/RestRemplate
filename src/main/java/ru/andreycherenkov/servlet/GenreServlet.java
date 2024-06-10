package ru.andreycherenkov.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.service.GenreService;
import ru.andreycherenkov.service.impl.GenreServiceImpl;
import ru.andreycherenkov.servlet.dto.IncomingGenreDto;
import ru.andreycherenkov.servlet.dto.OutgoingGenreDto;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapper;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapperImpl;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "GenreServlet", value = "/genre")
public class GenreServlet extends HttpServlet {

    private GenreService genreService;
    private GenreDtoMapper genreDtoMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        genreService = new GenreServiceImpl();
        genreDtoMapper = new GenreDtoMapperImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long genre_id = Long.parseLong(req.getParameter("id"));
        Genre genre = genreService.findById(genre_id);
        if (genre == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        OutgoingGenreDto outgoingGenreDto = genreDtoMapper.map(genre);

        String json = new Gson().toJson(outgoingGenreDto);
        resp.setContentType("application/json"); // Устанавливаем тип контента
        resp.setCharacterEncoding("UTF-8"); // Устанавливаем кодировку
        resp.getWriter().write(json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonBody = IOUtils.toString(req.getReader());
        IncomingGenreDto incomingGenreDto = new Gson().fromJson(jsonBody, IncomingGenreDto.class);
        Genre genre = new Genre();
        genre.setName(incomingGenreDto.name());
        Genre savedGenre = genreService.save(genre);
        OutgoingGenreDto outgoingGenreDto = genreDtoMapper.map(savedGenre);

        String json = new Gson().toJson(outgoingGenreDto);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
