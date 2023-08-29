package servlet;

import org.example.model.Author;
import org.example.service.impl.AuthorServiceImpl;
import org.example.servlet.AuthorServlet;
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
class AuthorServletTest {
    @Mock
    private AuthorServiceImpl authorService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    private AuthorServlet authorServlet;

    @BeforeEach
    public void setUp() {
        authorServlet = new AuthorServlet();
        authorServlet.init();
        authorServlet.setAuthorService(authorService);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testDoGetAllAuthors() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn(null);

        List<Author> authors = Arrays.asList(
                new Author(1L, "John Doe", 1978),
                new Author(2L, "Jane Smith", 1968)
        );

        when(authorService.findAll()).thenReturn(authors);
        when(response.getWriter()).thenReturn(writer);

        authorServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        List<Author> testAuthors = authorService.findAll();
        assertEquals(authors.size(), testAuthors.size());
    }

    @Test
    void testDoGetAuthorWithId() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("1");

        Author author = new Author(1L, "John Doe", 1958);

        when(authorService.findById(1L)).thenReturn(author);
        when(response.getWriter()).thenReturn(writer);

        authorServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        Author testAuthor = authorService.findById(1L);
        assertEquals(author.getFullName(), testAuthor.getFullName());
    }

    @Test
    void testDoDelete() throws ServletException, IOException {
        Long authorId = 1L;
        String idParam = authorId.toString();
        when(request.getParameter("id")).thenReturn(idParam);
        when(response.getWriter()).thenReturn(writer);

        Author author = new Author(authorId, "John Doe", 1978);
        when(authorService.findById(authorId)).thenReturn(author);
        when(authorService.delete(authorId)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        authorServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        assertEquals("Author deleted successfully.\r\n", stringWriter.toString());
    }
}
