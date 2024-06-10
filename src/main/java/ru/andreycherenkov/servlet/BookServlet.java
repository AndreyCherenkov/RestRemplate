package ru.andreycherenkov.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.service.AuthorService;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.service.impl.AuthorServiceImpl;
import ru.andreycherenkov.service.impl.BookServiceImpl;
import ru.andreycherenkov.servlet.dto.IncomingBookDto;
import ru.andreycherenkov.servlet.dto.OutgoingBookDto;
import ru.andreycherenkov.servlet.mapper.BookDtoMapper;
import ru.andreycherenkov.servlet.mapper.BookDtoMapperImpl;

import java.io.IOException;

@WebServlet(name = "BookServlet", value = "/book")
public class BookServlet extends HttpServlet {

    private BookService bookService;
    private AuthorService authorService;
    private BookDtoMapper bookDtoMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        bookService = new BookServiceImpl(); // Замените на вашу реализацию BookService
        bookDtoMapper = new BookDtoMapperImpl(); // Замените на вашу реализацию BookDtoMapper
        authorService = new AuthorServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long bookId = Long.parseLong(req.getParameter("id"));
        Book book = bookService.findById(bookId);
        if (book == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        OutgoingBookDto outgoingBookDto = bookDtoMapper.map(book);
        String json = new Gson().toJson(outgoingBookDto);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = IOUtils.toString(req.getReader());
        IncomingBookDto incomingBookDto = new Gson().fromJson(jsonBody, IncomingBookDto.class);

        Book book = new Book();
        book.setTitle(incomingBookDto.title());
        book.setIsbn(incomingBookDto.isbn());
        book.setPublicationYear(incomingBookDto.publicationYear());
        book.setGenreId(incomingBookDto.genre_id());

        // Добавляем авторов к книге
        incomingBookDto.authorIds().stream()
                .map(authorService::findById)
                .forEach(book::addAuthor);

        Book savedBook = bookService.save(book);

        OutgoingBookDto outgoingBookDto = bookDtoMapper.map(savedBook);
        String json = new Gson().toJson(outgoingBookDto);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
