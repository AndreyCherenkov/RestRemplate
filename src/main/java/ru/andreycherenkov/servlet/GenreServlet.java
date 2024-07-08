package ru.andreycherenkov.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.andreycherenkov.factory.GenreClassesFactory;
import ru.andreycherenkov.model.Genre;
import ru.andreycherenkov.service.GenreService;
import ru.andreycherenkov.servlet.dto.IncomingGenreDto;
import ru.andreycherenkov.servlet.dto.OutgoingGenreDto;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapper;
import ru.andreycherenkov.servlet.mapper.GenreDtoMapperImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "GenreServlet", value = "/genre")
public class GenreServlet extends HttpServlet {

    private GenreService genreService;
    private GenreDtoMapper genreDtoMapper;
    private ObjectMapper mapper;

    public GenreServlet() {
        this.genreService = GenreClassesFactory.getDefaultGenreService();
        this.genreDtoMapper = new GenreDtoMapperImpl();
        this.mapper = new ObjectMapper();
    }

    private static void setJson(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        setJson(resp);
        String response;

        try {
            if (req.getParameter("id").equals("all")) {
                List<Genre> genres = genreService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                response = mapper.writeValueAsString(genres);
            } else {
                Long getId = Long.parseLong(req.getParameter("id"));
                Genre genre = genreService.findById(getId);
                if (genre == null || genre.getId() == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response = mapper.writeValueAsString("Genre not found");
                } else {
                    OutgoingGenreDto outgoingGenreDto = genreDtoMapper.map(genre);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    response = mapper.writeValueAsString(outgoingGenreDto);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response = mapper.writeValueAsString(e);
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.write(response);
        printWriter.flush();
    }

    private static String mapToJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        setJson(resp);
        String requestBody = mapToJson(req);
        String response;
        Optional<IncomingGenreDto> incomingGenreDto;
        try {
            incomingGenreDto = Optional.ofNullable(mapper.readValue(requestBody, IncomingGenreDto.class));
            IncomingGenreDto incomingDto = incomingGenreDto.orElseThrow(IllegalArgumentException::new);
            Genre genre = genreDtoMapper.map(incomingDto);
            response = mapper.writeValueAsString(genreService.save(genre));
        } catch (JsonProcessingException e) {
            response = "JsonProcessingException";
        } catch (NullPointerException e) {
            response = "NullPointerException";
        } catch (Exception e) {
            response = "Unexpected exception";
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.write(response);
        printWriter.close();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String respone = "";

        try {
            Long id = Long.parseLong(req.getParameter("id"));
            genreService.deleteById(id);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            respone = mapper.writeValueAsString(e);
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.write(respone);
        printWriter.close();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJson(resp);
        String response = "";
        String requestBody = mapToJson(req);
        try {
            IncomingGenreDto incomingGenreDto = mapper.readValue(requestBody, IncomingGenreDto.class);
            genreService.save(genreDtoMapper.map(incomingGenreDto));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response = mapper.writeValueAsString(e);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response = mapper.writeValueAsString(e);
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.write(response);
        printWriter.close();
    }
}
