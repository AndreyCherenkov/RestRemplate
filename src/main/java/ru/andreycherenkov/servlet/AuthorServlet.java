package ru.andreycherenkov.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import ru.andreycherenkov.model.Author;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.service.impl.AuthorServiceImpl;
import ru.andreycherenkov.service.impl.BookServiceImpl;
import ru.andreycherenkov.servlet.dto.IncomingAuthorDto;
import ru.andreycherenkov.servlet.dto.OutgoingAuthorDto;
import ru.andreycherenkov.servlet.mapper.AuthorDtoMapper;
import ru.andreycherenkov.servlet.mapper.AuthorDtoMapperImpl;

import java.io.IOException;

@WebServlet(name = "AuthorServlet", value = "/author")
public class AuthorServlet extends HttpServlet {

    private AuthorService authorService;
    private BookService bookService;
    private AuthorDtoMapper authorDtoMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authorService = new AuthorServiceImpl();
        authorDtoMapper = new AuthorDtoMapperImpl();
        bookService = new BookServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long authorId = Long.parseLong(req.getParameter("id"));
        Author author = authorService.findById(authorId);
        if (author == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        OutgoingAuthorDto outgoingAuthorDto = authorDtoMapper.map(author);
        String json = new Gson().toJson(outgoingAuthorDto);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = IOUtils.toString(req.getReader());
        IncomingAuthorDto incomingAuthorDto = new Gson().fromJson(jsonBody, IncomingAuthorDto.class);

        Author author = new Author();
        author.setFirstName(incomingAuthorDto.firstName());
        author.setLastName(incomingAuthorDto.lastName());
        incomingAuthorDto.bookIds().stream()
                .map(bookService::findById)
                .forEach(author::addBook);
        Author savedAuthor = authorService.save(author);

        OutgoingAuthorDto outgoingAuthorDto = authorDtoMapper.map(savedAuthor);
        String json = new Gson().toJson(outgoingAuthorDto);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
