package ru.andreycherenkov.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.andreycherenkov.factory.AuthorClassesFactory;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.servlet.dto.IncomingAuthorDto;
import ru.andreycherenkov.servlet.dto.OutgoingAuthorDto;
import ru.andreycherenkov.servlet.mapper.AuthorDtoMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "AuthorServlet", value = "/author")
public class AuthorServlet extends HttpServlet {

    private AuthorService authorService;
    private AuthorDtoMapper authorDtoMapper;
    private ObjectMapper mapper;

    public AuthorServlet() {
        this.authorService = AuthorClassesFactory.getDefaultAuthorService();
        this.authorDtoMapper = AuthorClassesFactory.getDefaultAuthorDtoMapper();
        this.mapper = new ObjectMapper();
    }

    private static void setJson(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String response;

        try {
            if (req.getParameter("id").equals("all")) {
                List<Author> authors = authorService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                response = mapper.writeValueAsString(authors);
            } else {
                Long getId = Long.parseLong(req.getParameter("id"));
                Author author = authorService.findById(getId);
                if (author == null || author.getId() != getId) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response = mapper.writeValueAsString("No such author");
                } else {
                    OutgoingAuthorDto outgoingAuthorDto = authorDtoMapper.map(author);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    response = mapper.writeValueAsString(outgoingAuthorDto);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response = mapper.writeValueAsString(e);
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(response);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String requestBody = mapToJson(req);
        String response;
        Optional<IncomingAuthorDto> incomingAuthorDto;
        try {
            incomingAuthorDto = Optional.ofNullable(mapper.readValue(requestBody, IncomingAuthorDto.class));
            IncomingAuthorDto incomingAuthor = incomingAuthorDto.orElseThrow(IllegalArgumentException::new);
            Author author = authorDtoMapper.map(incomingAuthor);
            response = mapper.writeValueAsString(authorService.save(author));
        } catch (JsonProcessingException e) {
            response = "JsonProcessingException";
        } catch (NullPointerException e) {
            response = "NullPointerException";
        } catch (Exception e) {
            response = "Unexpected exception";
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(response);
        printWriter.close();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String response = "";
        String requestBody = mapToJson(req);
        try {
            IncomingAuthorDto incomingAuthorDto = mapper.readValue(requestBody, IncomingAuthorDto.class);
            Author author = authorDtoMapper.map(incomingAuthorDto);
            authorService.save(author);
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String response = "";
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            authorService.deleteById(id);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response = mapper.writeValueAsString(e);
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.write(response);
        printWriter.close();
    }
}
