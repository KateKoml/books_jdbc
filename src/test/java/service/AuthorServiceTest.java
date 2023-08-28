package service;

import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.service.AuthorService;
import org.example.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock
    AuthorRepository authorRepository;
    AuthorService authorService;

    @BeforeEach
    public void setUp() {
        authorService = new AuthorServiceImpl(authorRepository);
    }

    @Test
    void testFindAuthorById() {
        Author author = new Author();
        author.setId(1L);
        author.setFullName("John Doe");
        author.setYearOfBirth(1978);

        Mockito.when(authorRepository.findById(1L)).thenReturn(author);

        Assertions.assertEquals(author, authorService.findById(1L));
    }

    @Test
    void testFindOptionalAuthorById() {
        Author author = new Author();
        author.setId(1L);
        author.setFullName("John Doe");
        author.setYearOfBirth(1978);

        Mockito.when(authorRepository.findOptionalById(1L)).thenReturn(Optional.of(author));

        Assertions.assertEquals(Optional.of(author), authorService.findOptionalById(1L));
    }

    @Test
    void testFindAllAuthors() {
        List<Author> authors = new ArrayList<>();
        Author author1 = new Author();
        author1.setId(1L);
        author1.setFullName("John Doe");
        author1.setYearOfBirth(1978);

        Author author2 = new Author();
        author2.setId(2L);
        author2.setFullName("Jane Doe");
        author1.setYearOfBirth(1985);
        authors.add(author1);
        authors.add(author2);

        Mockito.when(authorRepository.findAll()).thenReturn(authors);

        Assertions.assertEquals(authors, authorService.findAll());
    }

    @Test
    void testCreateAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFullName("John Doe");
        author.setYearOfBirth(1978);

        Mockito.when(authorRepository.create(author)).thenReturn(author);

        Assertions.assertEquals(author, authorService.create(author));
    }

    @Test
    void testUpdateAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFullName("John Doe");
        author.setYearOfBirth(1978);

        Mockito.when(authorRepository.update(author)).thenReturn(author);

        Assertions.assertEquals(author, authorService.update(author));
    }

    @Test
    void testDeleteAuthor() {
        Mockito.when(authorRepository.delete(1L)).thenReturn(true);

        Assertions.assertTrue(authorService.delete(1L));
    }
}
