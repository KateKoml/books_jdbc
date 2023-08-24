package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ConnectionSetting;
import org.example.model.Book;
import org.example.repository.BookRepository;
import org.example.repository.impl.BookRepositoryImpl;
import org.example.service.BookService;
import org.example.service.impl.BookServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/books", loadOnStartup = 1)
public class BookServlet extends HttpServlet {
    private BookService bookService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ConnectionSetting connection = new ConnectionSetting();
        BookRepository bookRepository = new BookRepositoryImpl(connection);
        bookService = new BookServiceImpl(bookRepository);
        objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null) {
            Long bookId = Long.parseLong(id);
            Book book = bookService.findById(bookId);

            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), book);
            }

        } else {
            List<Book> books = bookService.findAll();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), books);

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Book book = objectMapper.readValue(request.getInputStream(), Book.class);
            bookService.create(book);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Book updatedBook = objectMapper.readValue(request.getInputStream(), Book.class);

            if (updatedBook == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                bookService.update(updatedBook);
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } else if (pathInfo.startsWith("/")) {
            Long bookId = Long.valueOf(pathInfo.substring(1));
            Integer genreId = Integer.valueOf(request.getParameter("genreId"));
            bookService.setBookGenre(bookId, genreId);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = bookService.delete(id);

            if (deleted) {
                String jsonResponse = objectMapper.writeValueAsString("Book deleted successfully.");
                response.getWriter().println(jsonResponse);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}
