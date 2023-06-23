package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    Film addLike(Long id, Long userId);

    Film deleteLike(Long id, Long userId);

    List<Film> getTopFilms(int count);
}
