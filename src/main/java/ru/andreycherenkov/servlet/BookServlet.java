package ru.andreycherenkov.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.andreycherenkov.factory.BookClassesFactory;
import ru.andreycherenkov.model.Book;
import ru.andreycherenkov.service.BookService;
import ru.andreycherenkov.servlet.dto.IncomingBookDto;
import ru.andreycherenkov.servlet.dto.OutgoingBookDto;
import ru.andreycherenkov.servlet.mapper.BookDtoMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@WebServlet(name = "BookServlet", value = "/book")
public class BookServlet extends HttpServlet {

    private BookService bookService;
    private BookDtoMapper bookDtoMapper;
    private ObjectMapper mapper;

    public BookServlet() {
        this.bookService = BookClassesFactory.getDefaultBookService();
        this.bookDtoMapper = BookClassesFactory.getDefaultBookDtoMapper();
        this.mapper = new ObjectMapper();
    }

    public BookServlet(BookService bookService, ObjectMapper mapper, BookDtoMapper bookDtoMapper) {
        this.bookService = Objects.requireNonNullElseGet(bookService, BookClassesFactory::getDefaultBookService);
        this.bookDtoMapper = Objects.requireNonNullElseGet(bookDtoMapper, BookClassesFactory::getDefaultBookDtoMapper);
        this.mapper = Objects.requireNonNullElseGet(mapper, ObjectMapper::new);
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
                List<Book> books = bookService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                response = mapper.writeValueAsString(books);
            } else {
                Long id = Long.parseLong(req.getParameter("id"));
                Book book = bookService.findById(id);
                if (book == null || book.getId() != null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response = mapper.writeValueAsString("Book not found");
                } else {
                    OutgoingBookDto outgoingBookDto = bookDtoMapper.map(book);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    response = mapper.writeValueAsString(outgoingBookDto);
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
        Optional<IncomingBookDto> incomingBookDto;
        try {
            incomingBookDto = Optional.ofNullable(mapper.readValue(requestBody, IncomingBookDto.class));
            IncomingBookDto incomingBook = incomingBookDto.orElseThrow(IllegalAccessError::new);
            Book book = bookDtoMapper.map(incomingBook);
            response = mapper.writeValueAsString(bookService.save(book));
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
        String requsetBody = mapToJson(req);
        try {
            IncomingBookDto incomingBookDto = mapper.readValue(requsetBody, IncomingBookDto.class);
            Book book = bookDtoMapper.map(incomingBookDto);
            bookService.save(book);
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
            bookService.deleteById(id);
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
