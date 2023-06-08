package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    List<Film> getFilms();

    Map<Long, Film> getMap();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    Film addLike(Long id, Long userId);

    Film deleteLike(Long id, Long userId);

    List<Film> getTop10Films(int count);
}
