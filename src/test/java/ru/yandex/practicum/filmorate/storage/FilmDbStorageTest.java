package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.modelFactory.ModelFactory;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final UserDbStorage userDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final ModelFactory modelFactory = ModelFactory.getInstance();
    Film film;
    Film film2;
    User user;
    User user2;


    @BeforeEach
    public void beforeEach() {
        film = modelFactory.createFilm();
        film2 = modelFactory.createFilm();
        modelFactory.setFilmName(film2);

        user = modelFactory.createUser();
        user2 = modelFactory.createUser();
        modelFactory.setNewUserData(user2);
    }

    @Test
    public void shouldAddFilm() {
        filmDbStorage.addFilm(film);
        assertEquals(film, filmDbStorage.getFilmById(film.getId()));
    }

    @Test
    public void shouldUpdateFilm() {
        filmDbStorage.addFilm(film);
        assertEquals(film, filmDbStorage.getFilmById(film.getId()));

        film.setName("updateName");
        filmDbStorage.updateFilm(film);
        assertEquals("updateName", filmDbStorage.getFilmById(film.getId()).getName());
    }

    @Test
    public void shouldAddDeleteLike() {
        filmDbStorage.addFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(1L, 2L);
        film.setLikes(likeDbStorage.getLikes(film.getId()));
        assertEquals(2, film.getLikes().size());

        filmDbStorage.deleteLike(1L, 1L);
        film.setLikes(likeDbStorage.getLikes(film.getId()));
        assertEquals(1, film.getLikes().size());
    }

    @Test
    public void shouldGetTop1Film() {
        filmDbStorage.addFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(1L, 2L);
        assertEquals(1, filmService.getTopFilms(1).get(0).getId());
    }
}