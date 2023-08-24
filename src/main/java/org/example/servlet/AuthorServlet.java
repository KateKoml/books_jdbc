package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ConnectionSetting;
import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.service.AuthorService;
import org.example.service.impl.AuthorServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/authors")
public class AuthorServlet extends HttpServlet {
    private AuthorService authorService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ConnectionSetting connection = new ConnectionSetting();
        AuthorRepository authorRepository = new AuthorRepositoryImpl(connection);
        authorService = new AuthorServiceImpl(authorRepository);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null) {
            Long authorId = Long.parseLong(id);
            Author author = authorService.findById(authorId);

            if (author == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), author);
            }

        } else {
            List<Author> authors = authorService.findAll();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), authors);

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Author author = objectMapper.readValue(request.getInputStream(), Author.class);
            objectMapper.writeValue(response.getWriter(), authorService.create(author));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Author updatedAuthor = objectMapper.readValue(request.getInputStream(), Author.class);

            if (updatedAuthor == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                objectMapper.writeValue(response.getWriter(), authorService.update(updatedAuthor));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null) {
            Long authorId = Long.parseLong(id);
            Author author = authorService.findById(authorId);

            if (author == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                boolean deleted = authorService.delete(authorId);
                if (deleted) {
                    response.getWriter().println("Author deleted successfully.");
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }
}

