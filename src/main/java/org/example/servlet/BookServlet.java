package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ConnectionSetting;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.BookRepository;
import org.example.repository.GenreRepository;
import org.example.repository.impl.BookRepositoryImpl;
import org.example.repository.impl.GenreRepositoryImpl;
import org.example.service.BookService;
import org.example.service.GenreService;
import org.example.service.impl.BookServiceImpl;
import org.example.service.impl.GenreServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private BookService bookService;
    private GenreService genreService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ConnectionSetting connection = new ConnectionSetting();
        BookRepository bookRepository = new BookRepositoryImpl(connection);
        GenreRepository genreRepository = new GenreRepositoryImpl(connection);

        bookService = new BookServiceImpl(bookRepository);
        genreService = new GenreServiceImpl(genreRepository);
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
            objectMapper.writeValue(response.getWriter(), bookService.create(book));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String id1 = request.getParameter("bookId");
        String id2 = request.getParameter("genreId");

        if (id1 != null && id2 != null) {
            Long bookId = Long.parseLong(id1);
            Integer genreId = Integer.parseInt(id2);

            Book book = bookService.findById(bookId);
            Genre genre = genreService.findById(genreId);

            if (book == null || genre == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                bookService.setBookGenre(bookId, genreId);
                response.getWriter().println("Genre was successfully added to book.");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            Book updatedBook = objectMapper.readValue(request.getInputStream(), Book.class);

            if (updatedBook == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                objectMapper.writeValue(response.getWriter(), bookService.update(updatedBook));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null) {
            Long bookId = Long.parseLong(id);
            Book book = bookService.findById(bookId);

            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                boolean deleted = bookService.delete(bookId);
                if (deleted) {
                    response.getWriter().println("Book deleted successfully.");
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }
}
