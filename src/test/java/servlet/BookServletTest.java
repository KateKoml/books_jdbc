package servlet;

import org.example.model.Book;
import org.example.service.impl.BookServiceImpl;
import org.example.servlet.BookServlet;
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
class BookServletTest {
    @Mock
    private BookServiceImpl bookService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    private BookServlet bookServlet;

    @BeforeEach
    public void setUp() {
        bookServlet = new BookServlet();
        bookServlet.init();
        bookServlet.setBookService(bookService);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testDoGetAllBooks() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn(null);

        List<Book> books = Arrays.asList(
                new Book(1L, "John Doe", 1956, 1L),
                new Book(2L, "Jane Smith", 1967, 1L)
        );

        when(bookService.findAll()).thenReturn(books);
        when(response.getWriter()).thenReturn(writer);

        bookServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        List<Book> testBooks = bookService.findAll();
        assertEquals(books.size(), testBooks.size());
    }

    @Test
    void testDoGetBookWithId() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("1");

        Book book = new Book(1L, "John Doe", 1958, 1L);

        when(bookService.findById(1L)).thenReturn(book);
        when(response.getWriter()).thenReturn(writer);

        bookServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        Book testBook = bookService.findById(1L);
        assertEquals(book.getName(), testBook.getName());
    }

    @Test
    void testDoDeleteBook() throws ServletException, IOException {
        Long bookId = 1L;
        String idParam = bookId.toString();
        when(request.getParameter("id")).thenReturn(idParam);
        when(response.getWriter()).thenReturn(writer);

        Book book = new Book(1L, "John Doe", 1958, 1L);
        when(bookService.findById(bookId)).thenReturn(book);
        when(bookService.delete(bookId)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        bookServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        assertEquals("Book deleted successfully.\r\n", stringWriter.toString());
    }
}
