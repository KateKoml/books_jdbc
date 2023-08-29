package servlet;

import org.example.model.Genre;
import org.example.service.impl.GenreServiceImpl;
import org.example.servlet.GenreServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServletTest {
    @Mock
    private GenreServiceImpl genreService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    private GenreServlet genreServlet;

    @BeforeEach
    public void setUp() {
        genreServlet = new GenreServlet();
        genreServlet.init();
        genreServlet.setGenreService(genreService);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testDoGetAllGenres() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn(null);

        List<Genre> genres = Arrays.asList(
                new Genre(1, "horror"),
                new Genre(2, "romance")
        );

        when(genreService.findAll()).thenReturn(genres);
        when(response.getWriter()).thenReturn(writer);

        genreServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        List<Genre> testGenres = genreService.findAll();
        assertEquals(genres.size(), testGenres.size());
    }

    @Test
    void testDoGetGenreWithId() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("1");

        Genre genre = new Genre(1, "horror");

        when(genreService.findById(1)).thenReturn(genre);
        when(response.getWriter()).thenReturn(writer);

        genreServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        Genre testGenre = genreService.findById(1);
        assertEquals(genre.getType(), testGenre.getType());
    }

    @Test
    void testDoDeleteGenre() throws ServletException, IOException {
        Integer genreId = 1;
        String idParam = genreId.toString();
        when(request.getParameter("id")).thenReturn(idParam);
        when(response.getWriter()).thenReturn(writer);

        Genre genre = new Genre(1, "horror");
        when(genreService.findById(genreId)).thenReturn(genre);
        when(genreService.delete(genreId)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        genreServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        assertEquals("Genre deleted successfully.\r\n", stringWriter.toString());
    }
}
