package ru.yandex.practicum.filmorate.modelFactory;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

public class ModelFactory {

    private static volatile ModelFactory factory;

    private ModelFactory() {

    }

    public static ModelFactory getInstance() {
        ModelFactory result = factory;

        if (result != null) {
            return result;
        }
        synchronized (ModelFactory.class) {
            if (factory == null) {
                factory = new ModelFactory();
            }
            return factory;
        }
    }

    public User createUser() {
        User user = new User();
        user.setName("user");
        user.setLogin("login");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setFriends(new HashSet<>());
        return user;
    }

    public Film createFilm() {
        Film film = new Film();
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
        return film;
    }

    public void setNewUserData(User user) {
        user.setName(user.getName() + Math.random());
        user.setLogin(user.getName() + user.getLogin());
        user.setEmail(user.getLogin() + "@mail.ru");
    }

    public void setFilmName(Film film) {
        film.setName(film.getName() + " " + Math.random());
    }
}
