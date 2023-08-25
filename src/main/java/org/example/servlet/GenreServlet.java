package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ConnectionSetting;
import org.example.model.Genre;
import org.example.repository.GenreRepository;
import org.example.repository.impl.GenreRepositoryImpl;
import org.example.service.GenreService;
import org.example.service.impl.GenreServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/genres")
public class GenreServlet extends HttpServlet {
    private GenreService genreService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ConnectionSetting connection = new ConnectionSetting();
        GenreRepository genreRepository = new GenreRepositoryImpl(connection);
        genreService = new GenreServiceImpl(genreRepository);
        objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null) {
            Integer genreId = Integer.parseInt(id);
            Genre genre = genreService.findById(genreId);

            if (genre == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), genre);
            }

        } else {
            List<Genre> genres = genreService.findAll();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), genres);

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Genre genre = objectMapper.readValue(request.getInputStream(), Genre.class);
            objectMapper.writeValue(response.getWriter(), genreService.create(genre));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Genre updatedGenre = objectMapper.readValue(request.getInputStream(), Genre.class);

            if (updatedGenre == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                objectMapper.writeValue(response.getWriter(), genreService.update(updatedGenre));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null) {
            Integer genreId = Integer.parseInt(id);
            Genre genre = genreService.findById(genreId);

            if (genre == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                boolean deleted = genreService.delete(genreId);
                if (deleted) {
                    response.getWriter().println("Genre deleted successfully.");
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }
}
