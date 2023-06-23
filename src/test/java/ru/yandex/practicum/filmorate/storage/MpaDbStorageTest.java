package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;
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
    }

    @Test
    public void shouldGetMpaList() {
        List<Mpa> mpaListTest = mpaDbStorage.getMpaList();
        assertEquals(5, mpaListTest.size());
    }

    @Test
    public void shouldGetMpa() {
        Mpa mpaTest = mpaDbStorage.getMpa(5);
        assertEquals("NC-17", mpaTest.getName());
    }

    @Test
    public void shouldAddMpaToFilm() {
        assertNull(film.getMpa());
        film.setMpa(Mpa.builder()
                .id(5L)
                .name("NC-17")
                .build());
        mpaDbStorage.addToFilm(film);
        assertNotNull(film.getMpa());
    }
}