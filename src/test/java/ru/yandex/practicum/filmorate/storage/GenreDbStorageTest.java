package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final GenreService genreService;
    private final GenreDbStorage genreDbStorage;
    Film film;


    @BeforeEach
    public void beforeEach() {
        film = new Film();
        film.setName("film");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(180);
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        film.setMpa(Mpa.builder()
                .id(5L)
                .name("NC-17")
                .build());
    }

    @Test
    public void shouldGetGenresList() {
        List<Genre> genreListTest = genreService.getGenres();
        assertEquals(6, genreListTest.size());
    }

    @Test
    public void shouldSetGenre() {
        assertTrue(film.getGenres().isEmpty());
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        assertEquals(1, film.getGenres().size());
    }

    @Test
    public void shouldGetGenreById() {
        Genre genreTest = genreService.getGenre(1);
        assertEquals("Комедия", genreTest.getName());
    }

    @Test
    public void shouldAddGenre() {
        assertTrue(film.getGenres().isEmpty());
        filmDbStorage.addFilm(film);
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        genreDbStorage.updateGenreForFilmInMemory(film);
        assertEquals(1, film.getGenres().size());
    }

    @Test
    public void shouldUpdateGenre() {
        assertTrue(film.getGenres().isEmpty());
        filmDbStorage.addFilm(film);
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        genreDbStorage.updateGenre(film);
        assertEquals(1, film.getGenres().size());
    }
}