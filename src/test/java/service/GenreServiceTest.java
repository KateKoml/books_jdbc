package service;

import org.example.model.Genre;
import org.example.repository.GenreRepository;
import org.example.service.GenreService;
import org.example.service.impl.GenreServiceImpl;
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
class GenreServiceTest {
    @Mock
    GenreRepository genreRepository;
    GenreService genreService;

    @BeforeEach
    public void setUp() {
        genreService = new GenreServiceImpl(genreRepository);
    }

    @Test
    void testFindGenreById() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setType("horror");

        Mockito.when(genreRepository.findById(1)).thenReturn(genre);

        Assertions.assertEquals(genre, genreService.findById(1));
    }

    @Test
    void testFindOptionalGenreById() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setType("horror");

        Mockito.when(genreRepository.findOptionalById(1)).thenReturn(Optional.of(genre));

        Assertions.assertEquals(Optional.of(genre), genreService.findOptionalById(1));
    }

    @Test
    void testFindAllGenres() {
        List<Genre> genres = new ArrayList<>();
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setType("horror");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setType("romance");
        genres.add(genre1);
        genres.add(genre2);

        Mockito.when(genreRepository.findAll()).thenReturn(genres);

        Assertions.assertEquals(genres, genreService.findAll());
    }

    @Test
    void testCreateGenre() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setType("horror");

        Mockito.when(genreRepository.create(genre)).thenReturn(genre);

        Assertions.assertEquals(genre, genreService.create(genre));
    }

    @Test
    void testUpdateGenre() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setType("horror");

        Mockito.when(genreRepository.update(genre)).thenReturn(genre);

        Assertions.assertEquals(genre, genreService.update(genre));
    }

    @Test
    void testDeleteGenre() {
        Mockito.when(genreRepository.delete(1)).thenReturn(true);

        Assertions.assertTrue(genreService.delete(1));
    }
}
